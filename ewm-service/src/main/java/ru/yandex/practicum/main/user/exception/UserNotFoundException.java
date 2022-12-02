package ru.yandex.practicum.main.user.exception;

import ru.yandex.practicum.main.error.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
