package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Comparator<Film> FILM_POPULARITY_DESC = Comparator.comparingInt(Film::getRate).reversed();
    private final FilmStorage filmStorage;
    private final UserService userService;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        film = filmStorage.createFilm(film);
        log.debug("Был создан фильм {} с id {}", film.getName(), film.getId());
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.getFilmById(film.getId()).orElseThrow(FilmNotExistException::new);
        film = filmStorage.updateFilm(film);
        log.debug("Был обновлён фильм {} с id {}", film.getName(), film.getId());
        return film;
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id).orElseThrow(FilmNotExistException::new);
    }

    public Film addLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(FilmNotExistException::new);
        User user = userService.getUserById(userId);

        filmStorage.addLike(id, userId);
        log.debug("Фильму с id {} поставил лайк пользователь {} с id {}", id, user.getName(), userId);
        return film;
    }

    public Film deleteLike(int id, int userId) {
        Film film = filmStorage.getFilmById(id).orElseThrow(FilmNotExistException::new);
        User user = userService.getUserById(userId);

        filmStorage.removeLike(id, userId);
        log.debug("Фильму с id {} убрал лайк пользователь {} с id {}", id, user.getName(), userId);
        return film;
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }
}
