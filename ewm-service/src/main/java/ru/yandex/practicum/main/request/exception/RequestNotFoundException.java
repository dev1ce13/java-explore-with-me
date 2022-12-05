package ru.yandex.practicum.main.request.exception;

import ru.yandex.practicum.main.error.exception.NotFoundException;

public class RequestNotFoundException extends NotFoundException {
    public RequestNotFoundException(String message) {
        super(message);
    }
}
