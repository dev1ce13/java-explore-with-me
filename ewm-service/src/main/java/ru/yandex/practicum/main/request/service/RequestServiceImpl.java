package ru.yandex.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.main.event.exception.EventNotFoundException;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.model.EventState;
import ru.yandex.practicum.main.event.repository.EventRepository;
import ru.yandex.practicum.main.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.main.request.exception.RequestNotFoundException;
import ru.yandex.practicum.main.request.mapper.RequestMapper;
import ru.yandex.practicum.main.request.model.Request;
import ru.yandex.practicum.main.request.model.RequestState;
import ru.yandex.practicum.main.request.repository.RequestRepository;
import ru.yandex.practicum.main.user.exception.UserNotFoundException;
import ru.yandex.practicum.main.user.model.User;
import ru.yandex.practicum.main.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> privateGetRequestsForEvent(int userId, int eventId) {
        return requestRepository.findAllByEvent_IdAndInitiatorId(eventId, userId).stream()
                .map(RequestMapper::mapToParticipationRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateConfirmRequest(int userId, int eventId, int reqId) {
        Request request = getById(reqId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        checkingInitiatorAccess(userId, event);
        if (event.getParticipantLimit() == event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new IllegalArgumentException("Participant limit exceeded");
        }
        if (eventId != request.getEvent().getId()) {
            throw new IllegalArgumentException(String
                    .format("Request with id=%s not applicable for event with id=%s", reqId, eventId));
        }
        request.setStatus(RequestState.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateRejectRequest(int userId, int eventId, int reqId) {
        Request request = getById(reqId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        checkingInitiatorAccess(userId, event);
        if (event.getId() != request.getEvent().getId()) {
            throw new IllegalArgumentException(String
                    .format("Request with id=%s not applicable for event with id=%s", reqId, eventId));
        }
        request.setStatus(RequestState.REJECTED);
        return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> privateGetRequests(int userId) {
        List<Request> requests = requestRepository.findAllByRequester_Id(userId);
        return requests.stream()
                .map(RequestMapper::mapToParticipationRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateAddRequest(int userId, int eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%s was not found.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
        if (event.getInitiator().getId() == userId) {
            throw new IllegalArgumentException("Initiator cannot participate in own event");
        }
        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId) != null) {
            throw new IllegalArgumentException("Request already exists");
        }
        Request request = RequestMapper.mapToRequestFromParticipationRequestDto(user, event);
        if (event.getParticipantLimit() == event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new IllegalArgumentException(String
                    .format("Application limit reached for event with id=%s", eventId));
        }
        if (!event.isRequestModeration()) {
            request.setStatus(RequestState.CONFIRMED);
        }
        return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateCancelRequest(int userId, int reqId) {
        Request request = getById(reqId);
        if (userId != request.getRequester().getId()) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not access to request with id=%s", userId, reqId));
        }
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    private Request getById(int id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(String
                        .format("Request with id=%s was not found.", id)));
    }

    private void checkingInitiatorAccess(int userId, Event event) {
        if (userId != event.getInitiator().getId()) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not access to event with id=%s", userId, event.getId()));
        }
    }
}
