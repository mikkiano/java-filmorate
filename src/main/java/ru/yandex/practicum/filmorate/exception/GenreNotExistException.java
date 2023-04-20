package ru.yandex.practicum.filmorate.exception;

public class GenreNotExistException extends RuntimeException {
    public GenreNotExistException() {
    }

    public GenreNotExistException(String message) {
        super(message);
    }

    public GenreNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenreNotExistException(Throwable cause) {
        super(cause);
    }
}
