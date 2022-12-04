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
import java.util.Optional;
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not have access in event with id=%s", userId, eventId));
        }
        List<Request> requests = requestRepository.findAllByEvent_Id(eventId);
        return requests.stream()
                .map(RequestMapper::mapToParticipationRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateConfirmRequest(int userId, int eventId, int reqId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not have access in event with id=%s", userId, eventId));
        }
        if (event.getParticipantLimit() == event.getConfirmedRequests() && event.getParticipantLimit() != 0) {
            throw new IllegalArgumentException("Participant limit exceeded");
        }
        Request request = getById(reqId);
        request.setStatus(RequestState.CONFIRMED);
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.save(event);
        return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateRejectRequest(int userId, int eventId, int reqId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        if (event.getInitiator().getId() != userId) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not have access in event with id=%s", userId, eventId));
        }
        Request request = getById(reqId);
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
        if (request.getRequester().getId() != userId) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s can not cancel request with id=%s", userId, reqId));
        }
        request.setStatus(RequestState.CANCELED);
        return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    private Request getById(int id) {
        Optional<Request> request = requestRepository.findById(id);
        if (request.isPresent()) {
            return request.get();
        } else {
            throw new RequestNotFoundException(String.format("Request with id=%s was not found.", id));
        }
    }
}
