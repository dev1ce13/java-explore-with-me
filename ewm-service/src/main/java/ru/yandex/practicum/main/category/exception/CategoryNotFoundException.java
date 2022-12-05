package ru.yandex.practicum.main.category.exception;

import lombok.Getter;
import ru.yandex.practicum.main.error.exception.NotFoundException;

@Getter
public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
