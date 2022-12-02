package ru.yandex.practicum.main.category.exception;

import ru.yandex.practicum.main.error.exception.ConflictException;

import java.sql.SQLException;

public class DuplicateCategoryNameException extends ConflictException {
    public DuplicateCategoryNameException(String message, SQLException root, String sql, String constraintName) {
        super(message, root, sql, constraintName);
    }
}
