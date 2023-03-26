package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmValidationTest {
    private static Validator validator;
    private static Film film;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        film = new Film();
    }

    @BeforeEach
    void setUpFilm() {
        film = new Film();
    }

    @Test
    public void emptyFilmNameTest() {
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void nonEmptyFilmNameTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void tooLongFilmDescriptionTest() {
        film.setName("Name");
        film.setDescription("DescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDesc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void borderFilmDescriptionTest() {
        film.setName("Name");
        film.setName("Name");
        film.setDescription("DescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDescDesc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void normalFilmDescriptionTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongFilmReleaseDateTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void normalFilmReleaseDateTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void borderFilmReleaseDateTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongFilmDurationTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void normalFilmDurationTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void borderFilmDurationTest() {
        film.setName("Name");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(1900, 1, 1));
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

}