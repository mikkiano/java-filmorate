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

class UserValidationTest {
    private static Validator validator;
    private static User user;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
    }

    @BeforeEach
    void setUpUser() {
        user = new User();
    }

    @Test
    public void emptyEmailTest() {
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void nonEmptyEmailTest() {
        user.setEmail("Login@ya.ru");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void wrongEmailTest() {
        user.setEmail("Login@ +_ya.ru");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void emptyLoginTest() {
        user.setEmail("Login@ya.ru");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void spaceLoginTest() {
        user.setEmail("Login@ya.ru");
        user.setLogin("Log in");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void blankLoginTest() {
        user.setEmail("Login@ya.ru");
        user.setLogin("");
        user.setName("Name");
        user.setBirthday(LocalDate.now());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void emptyBirthdayTest() {
        user.setEmail("Login@ya.ru");
        user.setLogin("Login");
        user.setName("Name");

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void futureBirthdayTest() {
        user.setEmail("Login@ya.ru");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void normalBirthdayTest() {
        user.setEmail("Login@ya.ru");
        user.setLogin("Login");
        user.setName("Name");
        user.setBirthday(LocalDate.now().minusYears(18));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }
}