package ru.yandex.practicum.main.request.mapper;

import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.main.request.model.Request;
import ru.yandex.practicum.main.request.model.RequestState;
import ru.yandex.practicum.main.user.model.User;

import java.time.LocalDateTime;

public class RequestMapper {

    public static Request mapToRequestFromParticipationRequestDto(User user, Event event) {
        return new Request(LocalDateTime.now(), event, 0, user, RequestState.PENDING);
    }

    public static ParticipationRequestDto mapToParticipationRequestDtoFromRequest(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
