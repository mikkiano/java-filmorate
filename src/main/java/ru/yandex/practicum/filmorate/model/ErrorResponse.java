package ru.yandex.practicum.filmorate.model;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

}