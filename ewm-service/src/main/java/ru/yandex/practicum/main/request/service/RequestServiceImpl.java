package ru.yandex.practicum.main.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

import javax.transaction.Transactional;
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
    @Transactional
    public List<ParticipationRequestDto> privateGetRequestsForEvent(int userId, int eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            if (event.get().getInitiator().getId() == userId) {
                List<Request> requests = requestRepository.findAllByEvent_Id(eventId);
                return requests.stream()
                        .map(RequestMapper::mapToParticipationRequestDtoFromRequest)
                        .collect(Collectors.toList());
            } else {
                throw new IllegalArgumentException(String
                        .format("User with id=%s not have access in event with id=%s", userId, eventId));
            }
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateConfirmRequest(int userId, int eventId, int reqId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            if (event.get().getInitiator().getId() == userId) {
                if (event.get().getParticipantLimit() != event.get().getConfirmedRequests()
                        || event.get().getParticipantLimit() == 0) {
                    Request request = getById(reqId);
                    request.setStatus(RequestState.CONFIRMED);
                    event.get().setConfirmedRequests(event.get().getConfirmedRequests() + 1);
                    eventRepository.save(event.get());
                    return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
                } else {
                    throw new IllegalArgumentException("Participant limit exceeded");
                }
            } else {
                throw new IllegalArgumentException(String
                        .format("User with id=%s not have access in event with id=%s", userId, eventId));
            }
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateRejectRequest(int userId, int eventId, int reqId) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            if (event.get().getInitiator().getId() == userId) {
                Request request = getById(reqId);
                request.setStatus(RequestState.REJECTED);
                return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
            } else {
                throw new IllegalArgumentException(String
                        .format("User with id=%s not have access in event with id=%s", userId, eventId));
            }
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> privateGetRequests(int userId) {
        List<Request> requests = requestRepository.findAllByRequester_Id(userId);
        return requests.stream()
                .map(RequestMapper::mapToParticipationRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateAddRequest(int userId, int eventId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Event> event = eventRepository.findById(eventId);
            if (event.isPresent() && event.get().getState().equals(EventState.PUBLISHED)) {
                if (event.get().getInitiator().getId() != userId) {
                    Request request = RequestMapper.mapToRequestFromParticipationRequestDto(user.get(), event.get());
                    if (event.get().getParticipantLimit() == event.get().getConfirmedRequests()
                            && event.get().getParticipantLimit() != 0) {
                        throw new IllegalArgumentException(String
                                .format("Application limit reached for event with id=%s", eventId));
                    } else if (!event.get().isRequestModeration()) {
                        request.setStatus(RequestState.CONFIRMED);
                    }
                    return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
                } else {
                    throw new IllegalArgumentException("Initiator cannot participate in own event");
                }
            } else {
                throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
            }
        } else {
            throw new UserNotFoundException(String.format("User with id=%s was not found.", userId));
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto privateCancelRequest(int userId, int reqId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Request request = getById(reqId);
            request.setStatus(RequestState.CANCELED);
            return RequestMapper.mapToParticipationRequestDtoFromRequest(requestRepository.save(request));
        } else {
            throw new UserNotFoundException(String.format("User with id=%s was not found.", userId));
        }
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
