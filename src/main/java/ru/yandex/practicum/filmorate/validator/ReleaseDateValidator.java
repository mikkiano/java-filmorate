package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ReleaseDateConstraint contactNumber) {
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        return !releaseDate.isBefore(FIRST_FILM_RELEASE_DATE);
    }

}