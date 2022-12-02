package ru.yandex.practicum.main.request.service;

import ru.yandex.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> privateGetRequestsForEvent(int userId, int eventId);

    ParticipationRequestDto privateConfirmRequest(int userId, int eventId, int reqId);

    ParticipationRequestDto privateRejectRequest(int userId, int eventId, int reqId);

    List<ParticipationRequestDto> privateGetRequests(int userId);

    ParticipationRequestDto privateAddRequest(int userId, int eventId);

    ParticipationRequestDto privateCancelRequest(int userId, int reqId);
}
