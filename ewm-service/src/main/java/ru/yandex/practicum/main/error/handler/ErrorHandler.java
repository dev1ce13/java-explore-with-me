package ru.yandex.practicum.main.error.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.main.category.controllers.AdminCategoryController;
import ru.yandex.practicum.main.category.controllers.PublicCategoryController;
import ru.yandex.practicum.main.compilation.controllers.AdminCompilationController;
import ru.yandex.practicum.main.compilation.controllers.PublicCompilationController;
import ru.yandex.practicum.main.error.exception.ConflictException;
import ru.yandex.practicum.main.error.exception.NotFoundException;
import ru.yandex.practicum.main.error.model.ApiError;
import ru.yandex.practicum.main.event.controllers.AdminController;
import ru.yandex.practicum.main.event.controllers.PrivateController;
import ru.yandex.practicum.main.event.controllers.PublicController;
import ru.yandex.practicum.main.request.controller.PrivateRequestController;
import ru.yandex.practicum.main.user.controllers.AdminUserController;

import java.time.LocalDateTime;
import java.util.ArrayList;

@RestControllerAdvice(assignableTypes = {
        AdminCategoryController.class,
        PublicCategoryController.class,
        AdminCompilationController.class,
        PublicCompilationController.class,
        AdminController.class,
        PrivateController.class,
        PublicController.class,
        PrivateRequestController.class,
        AdminUserController.class
})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleNotFoundException(final NotFoundException e) {
        ApiError apiError = ApiError.builder()
                .errors(new ArrayList<>())
                .reason("The required object was not found.")
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(
                apiError,
                apiError.getStatus()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleConflictException(final ConflictException e) {
        ApiError apiError = ApiError.builder()
                .errors(new ArrayList<>())
                .reason("Integrity constraint has been violated")
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .message(e.getMessage() + e.getSQL() + e.getConstraintName() + e.getSQLException().getMessage())
                .build();
        return new ResponseEntity<>(
                apiError,
                apiError.getStatus()
        );
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleIllegalArgumentException(final IllegalArgumentException e) {
        ApiError apiError = ApiError.builder()
                .errors(new ArrayList<>())
                .reason("Only pending or canceled events can be changed")
                .status(HttpStatus.FORBIDDEN)
                .timestamp(LocalDateTime.now())
                .message(e.getMessage())
                .build();
        return new ResponseEntity<>(
                apiError,
                apiError.getStatus()
        );
    }
}
