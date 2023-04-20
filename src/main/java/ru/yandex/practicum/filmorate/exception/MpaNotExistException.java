package ru.yandex.practicum.filmorate.exception;

public class MpaNotExistException extends RuntimeException {
    public MpaNotExistException() {
    }

    public MpaNotExistException(String message) {
        super(message);
    }

    public MpaNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public MpaNotExistException(Throwable cause) {
        super(cause);
    }
}
