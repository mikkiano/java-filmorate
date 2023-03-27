package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Comparator<Film> FILM_POPULARITY_DESC = Comparator.comparingInt((Film o) -> o.getLikes().size()).reversed();
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
        try {
            film = filmStorage.updateFilm(film);
            log.debug("Был обновлён фильм {} с id {}", film.getName(), film.getId());
            return film;
        } catch (FilmNotExistException e) {
            log.error("Фильма с id {} не существует", film.getId());
            throw e;
        }
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(int id, int userId) {
        try {
            Film film = filmStorage.getFilmById(id);
            User user = userService.getUserById(userId);

            film.getLikes().add(userId);
            log.debug("Фильму с id {} поставил лайк пользователь {} с id {}", id, user.getName(), userId);
            return film;
        } catch (UserNotExistException | FilmNotExistException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public Film deleteLike(int id, int userId) {
        try {
            Film film = filmStorage.getFilmById(id);
            User user = userService.getUserById(userId);

            film.getLikes().remove(userId);
            log.debug("Фильму с id {} убрал лайк пользователь {} с id {}", id, user.getName(), userId);
            return film;
        } catch (UserNotExistException | FilmNotExistException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.findAll()
                .stream()
                .sorted(FILM_POPULARITY_DESC)
                .limit(count)
                .collect(Collectors.toList());
    }
}
