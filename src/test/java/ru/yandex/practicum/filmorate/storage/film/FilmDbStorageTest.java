package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @BeforeEach
    public void beforeEach() {
        filmStorage.createFilm(
                Film.builder()
                        .name("name1")
                        .description("desc1")
                        .releaseDate(LocalDate.now())
                        .duration(100)
                        .rate(2)
                        .mpa(Mpa.builder().id(1).build())
                        .build());
        filmStorage.createFilm(
                Film.builder()
                        .name("name2")
                        .description("desc2")
                        .releaseDate(LocalDate.now())
                        .duration(120)
                        .rate(3)
                        .mpa(Mpa.builder().id(2).build())
                        .build());
        userStorage.createUser(
                User.builder()
                        .email("test@mail.com")
                        .login("test")
                        .name("test")
                        .birthday(LocalDate.of(2002, 12, 1))
                        .build());
        userStorage.createUser(
                User.builder()
                        .email("test@ya.ru")
                        .login("test2")
                        .name("test2")
                        .birthday(LocalDate.of(2000, 8, 19))
                        .build());
    }

    @Test
    void findAll() {
        List<Film> films = filmStorage.findAll();
        assertNotNull(films);
    }

    @Test
    @DirtiesContext
    void createFilm() {
        Film film = Film.builder()
                .name("film1")
                .description("desc")
                .releaseDate(LocalDate.now())
                .duration(123)
                .rate(0)
                .mpa(Mpa.builder().id(1).build())
                .build();

        assertNotNull(film.getId());
    }

    @Test
    @DirtiesContext
    void updateFilm() {
        Film film = filmStorage.getFilmById(1).get();
        film.setName("Changed");
        filmStorage.updateFilm(film);

        assertEquals(filmStorage.getFilmById(1).get().getName(), "Changed");
    }

    @Test
    void getFilmById() {
        Film film = filmStorage.getFilmById(1).get();
        assertNotNull(film);
    }

    @Test
    @DirtiesContext
    void addLike() {
        List<Film> films1 = filmStorage.getMostPopularFilms(1);
        filmStorage.addLike(1, 1);
        List<Film> films2 = filmStorage.getMostPopularFilms(1);
        assertNotEquals(films1, films2);
    }

    @Test
    @DirtiesContext
    void removeLike() {
        List<Film> films1 = filmStorage.getMostPopularFilms(1);
        filmStorage.addLike(1, 1);
        filmStorage.removeLike(1, 1);
        List<Film> films2 = filmStorage.getMostPopularFilms(1);
        assertEquals(films1, films2);
    }

    @Test
    void getMostPopularFilms() {
        List<Film> films = filmStorage.getMostPopularFilms(10);
        assertNotNull(films);
    }
}