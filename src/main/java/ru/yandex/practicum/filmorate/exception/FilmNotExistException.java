package ru.yandex.practicum.filmorate.exception;

public class FilmNotExistException extends RuntimeException {
    public FilmNotExistException() {
    }

    public FilmNotExistException(String message) {
        super(message);
    }

    public FilmNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilmNotExistException(Throwable cause) {
        super(cause);
    }
}
