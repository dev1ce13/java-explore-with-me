package ru.yandex.practicum.main.event.exception;

import ru.yandex.practicum.main.error.exception.NotFoundException;

public class EventNotFoundException extends NotFoundException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
