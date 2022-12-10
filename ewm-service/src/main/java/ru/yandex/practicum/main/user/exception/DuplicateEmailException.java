package ru.yandex.practicum.main.user.exception;

import ru.yandex.practicum.main.error.exception.ConflictException;

import java.sql.SQLException;

public class DuplicateEmailException extends ConflictException {
    public DuplicateEmailException(String message, SQLException root, String sql, String constraintName) {
        super(message, root, sql, constraintName);
    }
}
