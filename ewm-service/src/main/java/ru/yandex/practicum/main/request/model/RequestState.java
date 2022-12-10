package ru.yandex.practicum.main.request.model;

import java.util.Optional;

public enum RequestState {
    PENDING,
    CONFIRMED,
    CANCELED,
    REJECTED;

    public static Optional<RequestState> from(String stringState) {
        for (RequestState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
