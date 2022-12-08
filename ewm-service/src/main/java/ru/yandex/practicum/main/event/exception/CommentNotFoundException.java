package ru.yandex.practicum.main.event.exception;

import ru.yandex.practicum.main.error.exception.NotFoundException;

public class CommentNotFoundException extends NotFoundException {
    public CommentNotFoundException(String message) {
        super(message);
    }
}
