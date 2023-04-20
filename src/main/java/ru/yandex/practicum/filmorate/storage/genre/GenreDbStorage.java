package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private static Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .build();
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT ID, NAME FROM GENRE";

        return jdbcTemplate.query(sqlQuery, GenreDbStorage::mapRow);
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE WHERE ID = ?", id);

        if (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("ID"),
                    genreRows.getString("NAME"));
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }
}
