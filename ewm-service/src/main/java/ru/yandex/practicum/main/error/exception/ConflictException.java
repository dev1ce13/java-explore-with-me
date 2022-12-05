package ru.yandex.practicum.main.error.exception;

import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;

public class ConflictException extends ConstraintViolationException {
    public ConflictException(String message, SQLException root, String sql, String constraintName) {
        super(message, root, sql, constraintName);
    }
}
