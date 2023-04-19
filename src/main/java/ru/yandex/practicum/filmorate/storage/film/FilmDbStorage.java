package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT F.*, M.ID AS MPA_ID, M.NAME AS MPA_NAME FROM FILM F JOIN MPA M on F.MPA_ID = M.ID";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        return films;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, new String[]{"ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, (film.getRate() != null) ? film.getRate() : 0);
            preparedStatement.setInt(6, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);
        int insertedId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> addFilmsGenre(insertedId, genre.getId()));
        }
        film.setId(insertedId);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, MPA_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                (film.getRate() != null) ? film.getRate() : 0,
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            deleteFilmsGenre(film.getId());
            film.getGenres().forEach(genre -> addFilmsGenre(film.getId(), genre.getId()));
        } else {
            deleteFilmsGenre(film.getId());
        }
        film.setGenres(getFilmsGenre(film.getId()));
        return film;
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE ID = ?", id);
        if (filmRows.next()) {
            Film film = Film.builder()
                    .id(filmRows.getInt("ID"))
                    .name(filmRows.getString("NAME"))
                    .description(filmRows.getString("DESCRIPTION"))
                    .releaseDate((Objects.requireNonNull(filmRows.getDate("RELEASE_DATE"))).toLocalDate())
                    .duration(filmRows.getInt("DURATION"))
                    .rate(filmRows.getInt("RATE"))
                    .mpa(mpaStorage.getMpaById(filmRows.getInt("MPA_ID")).orElseThrow(ValidationException::new))
                    .genres(getFilmsGenre(filmRows.getInt("ID")))
                    .build();
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    private List<Genre> getFilmsGenre(int id) {
        String sql = "SELECT G.ID, G.NAME FROM FILM_GENRE FG JOIN GENRE G ON G.ID = fg.GENRE_ID WHERE FILM_ID = ? ORDER BY G.ID;";
        ArrayList<Genre> genres = new ArrayList<>(jdbcTemplate.query(sql, (rs, rowNum) -> Genre.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .build(), id));
        return genres;
    }

    private void addFilmsGenre(int id, int genreId) {
        String acceptance = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
        SqlRowSet userFriendsRows = jdbcTemplate.queryForRowSet(acceptance, id, genreId);
        if (!userFriendsRows.next()) {
            String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, id, genreId);
        }
    }

    private void deleteFilmsGenre(int id) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public void addLike(int id, int userId) {
        if (likeExists(id, userId)) {
            return;
        }
        String sqlFilmsLikes = "INSERT INTO FILM_LIKE (FILM_ID, USER_ID) VALUES (?,?)";
        jdbcTemplate.update(sqlFilmsLikes, id, userId);
        String sqlFilms = "UPDATE FILM SET RATE = RATE + 1 WHERE ID = ?";
        jdbcTemplate.update(sqlFilms, id);
    }

    public void removeLike(int id, int userId) {
        if (!likeExists(id, userId)) {
            return;
        }
        String sqlFilmsLikes = "DELETE FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sqlFilmsLikes, id, userId);
        String sqlFilms = "UPDATE FILM SET RATE = RATE - 1 WHERE ID = ?";
        jdbcTemplate.update(sqlFilms, id);
    }

    private boolean likeExists(int filmId, int userId) {
        String likeExists = "SELECT 1 FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID = ?";
        SqlRowSet userFriendsRows = jdbcTemplate.queryForRowSet(likeExists, filmId, userId);
        return userFriendsRows.next();
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT F.*, M.ID AS MPA_ID, M.NAME AS MPA_NAME " +
                "FROM FILM F JOIN MPA M on F.MPA_ID = M.ID " +
                "ORDER BY RATE DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("ID"))
                .name((resultSet.getString("NAME")))
                .description((resultSet.getString("DESCRIPTION")))
                .releaseDate((resultSet.getDate("RELEASE_DATE")).toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .rate(resultSet.getInt("RATE"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME"))
                        .build())
                .genres(getFilmsGenre(resultSet.getInt("ID")))
                .build();
    }
}
