package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotExistException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody @Valid Film film) {
        if (isReleaseDateValid(film)) {
            Integer newId = id++;
            film.setId(newId);
            films.put(newId, film);
            log.debug("Был создан фильм {} с id {}", film.getName(), film.getId());
            return film;
        } else {
            log.error("Ошибка валидации даты выхода.");
            throw new ValidationException();
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        if (isReleaseDateValid(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
                log.debug("Был обновлён фильм {} с id {}", film.getName(), film.getId());
            } else {
                log.error("Фильма с id {} не существует", film.getId());
                throw new FilmNotExistException();
            }
            return film;
        } else {
            log.error("Ошибка валидации даты выхода.");
            throw new ValidationException();
        }
    }

    private boolean isReleaseDateValid(Film film) {
        return !film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE);
    }
}
