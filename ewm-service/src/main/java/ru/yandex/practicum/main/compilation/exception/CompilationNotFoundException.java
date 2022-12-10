package ru.yandex.practicum.main.compilation.exception;

import ru.yandex.practicum.main.error.exception.NotFoundException;

public class CompilationNotFoundException extends NotFoundException {
    public CompilationNotFoundException(String message) {
        super(message);
    }
}
