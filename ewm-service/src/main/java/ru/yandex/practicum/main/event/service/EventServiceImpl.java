package ru.yandex.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.main.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.main.category.model.Category;
import ru.yandex.practicum.main.category.repository.CategoryRepository;
import ru.yandex.practicum.main.event.client.StatClient;
import ru.yandex.practicum.main.event.client.dto.RequestDto;
import ru.yandex.practicum.main.event.dto.*;
import ru.yandex.practicum.main.event.exception.CommentNotFoundException;
import ru.yandex.practicum.main.event.exception.EventNotFoundException;
import ru.yandex.practicum.main.event.mapper.CommentMapper;
import ru.yandex.practicum.main.event.mapper.EventMapper;
import ru.yandex.practicum.main.event.model.Comment;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.model.EventSort;
import ru.yandex.practicum.main.event.model.EventState;
import ru.yandex.practicum.main.event.repository.CommentRepository;
import ru.yandex.practicum.main.event.repository.EventRepository;
import ru.yandex.practicum.main.request.model.Request;
import ru.yandex.practicum.main.request.model.RequestState;
import ru.yandex.practicum.main.request.repository.RequestRepository;
import ru.yandex.practicum.main.user.exception.UserNotFoundException;
import ru.yandex.practicum.main.user.model.User;
import ru.yandex.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> adminGetEvents(List<Integer> users,
                                             List<EventState> states,
                                             List<Integer> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             int from,
                                             int size) {
        List<Event> events = eventRepository.findAll();
        if (users != null) {
            events = events.stream()
                    .filter(event -> users.contains(event.getInitiator().getId()))
                    .collect(Collectors.toList());
        }
        if (states != null) {
            events = events.stream()
                    .filter(event -> states.contains(event.getState()))
                    .collect(Collectors.toList());
        }
        if (categories != null) {
            events = events.stream()
                    .filter(event -> categories.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (rangeStart == null && rangeEnd == null) {
            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else {
            if (rangeStart != null) {
                events = events.stream()
                        .filter(event -> event.getEventDate().isAfter(rangeStart)
                                || event.getEventDate().equals(rangeStart))
                        .collect(Collectors.toList());
            }
            if (rangeEnd != null) {
                events = events.stream()
                        .filter(event -> event.getEventDate().isBefore(rangeEnd)
                                || event.getEventDate().equals(rangeEnd))
                        .collect(Collectors.toList());
            }
        }
        checkingFromParameter(from, events.size());
        return events.subList(from, events.size())
                .stream()
                .limit(size)
                .map(EventMapper::mapToEventFullDtoFromEvent)
                .peek(eventFullDto -> eventFullDto.setComments(
                        commentRepository.findAllByEvent_Id(eventFullDto.getId()).stream()
                                .map(CommentMapper::mapToCommentDtoFromComment)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEvent(int id, AdminUpdateEventRequestDto adminUpdateEventRequestDto) {
        Event event = getById(id);
        if (adminUpdateEventRequestDto.getAnnotation() != null) {
            event.setAnnotation(adminUpdateEventRequestDto.getAnnotation());
        }
        if (adminUpdateEventRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(adminUpdateEventRequestDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(String
                            .format("Category with id=%s was not found.", adminUpdateEventRequestDto.getCategory())));
            event.setCategory(category);
        }
        if (adminUpdateEventRequestDto.getDescription() != null) {
            event.setDescription(adminUpdateEventRequestDto.getDescription());
        }
        if (adminUpdateEventRequestDto.getEventDate() != null) {
            event.setEventDate(adminUpdateEventRequestDto.getEventDate());
        }
        if (adminUpdateEventRequestDto.getLocation() != null) {
            if (adminUpdateEventRequestDto.getLocation().getLat() != null) {
                event.setLat(adminUpdateEventRequestDto.getLocation().getLat());
            }
            if (adminUpdateEventRequestDto.getLocation().getLon() != null) {
                event.setLon(adminUpdateEventRequestDto.getLocation().getLon());
            }
        }
        if (adminUpdateEventRequestDto.getPaid() != null) {
            event.setPaid(adminUpdateEventRequestDto.getPaid());
        }
        if (adminUpdateEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(adminUpdateEventRequestDto.getParticipantLimit());
        }
        if (adminUpdateEventRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(adminUpdateEventRequestDto.getRequestModeration());
        }
        if (adminUpdateEventRequestDto.getTitle() != null) {
            event.setTitle(adminUpdateEventRequestDto.getTitle());
        }
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto adminPublishEvent(int id) {
        Event event = getById(id);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new IllegalArgumentException(String.format("Сan not publish event with state=%s", event.getState()));
        }
        checkingEventDate(event.getEventDate(), "admin");
        event.setState(EventState.PUBLISHED);
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto adminRejectEvent(int id) {
        Event event = getById(id);
        if (event.getState().equals(EventState.PUBLISHED) || event.getState().equals(EventState.CANCELED)) {
            throw new IllegalArgumentException(String
                    .format("Сan not cancel event with state=%s", event.getState()));
        }
        event.setState(EventState.CANCELED);
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> privateGetEvents(int id, int from, int size) {
        List<Event> events = eventRepository.findAllByInitiator_Id(id);
        checkingFromParameter(from, events.size());
        return events.subList(from, events.size())
                .stream()
                .limit(size)
                .map(EventMapper::mapToEventShortDtoFromEvent)
                .peek(eventShortDto -> eventShortDto.setComments(
                        commentRepository.findAllByEvent_Id(
                                        eventShortDto.getId()).stream()
                                .map(CommentMapper::mapToCommentDtoFromComment)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto privateUpdateEvent(int id, UpdateEventRequestDto updateEventRequestDto) {
        Event event = getById(updateEventRequestDto.getEventId());
        checkingInitiatorAccess(id, event);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("The event has already been published");
        }
        if (updateEventRequestDto.getEventDate() != null) {
            checkingEventDate(updateEventRequestDto.getEventDate(), "private");
            event.setEventDate(updateEventRequestDto.getEventDate());
        }
        if (updateEventRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventRequestDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(String
                            .format("Category with id=%s was not found.", updateEventRequestDto.getCategory())));
            event.setCategory(category);
        }
        if (updateEventRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventRequestDto.getAnnotation());
        }
        if (updateEventRequestDto.getDescription() != null) {
            event.setDescription(updateEventRequestDto.getDescription());
        }
        if (updateEventRequestDto.getPaid() != null) {
            event.setPaid(updateEventRequestDto.getPaid());
        }
        if (updateEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequestDto.getParticipantLimit());
        }
        if (updateEventRequestDto.getTitle() != null) {
            event.setTitle(updateEventRequestDto.getTitle());
        }
        event.setState(EventState.PENDING);
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto privateCreateEvent(int id, NewEventDto newEventDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%s was not found.", id)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(String
                        .format("Category with id=%s was not found.", id)));
        checkingEventDate(newEventDto.getEventDate(), "private");
        Event event = EventMapper.mapToEventFromNewEventDto(newEventDto, user, category);
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto privateGetEventById(int userId, int eventId) {
        Event event = getById(eventId);
        checkingInitiatorAccess(userId, event);
        EventFullDto outputEvent = EventMapper.mapToEventFullDtoFromEvent(event);
        outputEvent.setComments(commentRepository.findAllByEvent_Id(event.getId())
                .stream()
                .map(CommentMapper::mapToCommentDtoFromComment)
                .collect(Collectors.toList()));
        return outputEvent;
    }

    @Override
    @Transactional
    public EventFullDto privateRejectEventById(int userId, int eventId) {
        Event event = getById(eventId);
        checkingInitiatorAccess(userId, event);
        if (!event.getState().equals(EventState.PENDING)) {
            throw new IllegalArgumentException(String
                    .format("Event with id=%s cannot be canceled", eventId));
        }
        event.setState(EventState.CANCELED);
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> publicGetEvents(String text,
                                               List<Integer> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               EventSort eventSort,
                                               int from,
                                               int size,
                                               String remoteAddr,
                                               String requestURI) {
        List<Event> events = eventRepository.findAllForPublic("%" + text + "%",
                "%" + text + "%",
                String.valueOf(EventState.PUBLISHED));
        if (categories != null) {
            events = events.stream()
                    .filter(event -> categories.contains(event.getCategory().getId()))
                    .collect(Collectors.toList());
        }
        if (paid != null) {
            events = events.stream()
                    .filter(event -> event.isPaid() == paid)
                    .collect(Collectors.toList());
        }
        if (rangeStart == null && rangeEnd == null) {
            events = events.stream()
                    .filter(event -> event.getEventDate().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else {
            if (rangeStart != null) {
                events = events.stream()
                        .filter(event -> event.getEventDate().isAfter(rangeStart)
                                || event.getEventDate().equals(rangeStart))
                        .collect(Collectors.toList());
            }
            if (rangeEnd != null) {
                events = events.stream()
                        .filter(event -> event.getEventDate().isBefore(rangeEnd)
                                || event.getEventDate().equals(rangeEnd))
                        .collect(Collectors.toList());
            }
        }
        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> (event.getConfirmedRequests() < event.getParticipantLimit())
                            && event.getParticipantLimit() != 0)
                    .collect(Collectors.toList());
        }
        checkingFromParameter(from, events.size());
        if (eventSort != null) {
            if (eventSort.equals(EventSort.VIEWS)) {
                events = events.stream()
                        .sorted(Comparator.comparingInt(Event::getViews))
                        .collect(Collectors.toList());
            } else {
                events = events.stream()
                        .sorted((e1, e2) -> {
                            LocalDateTime eventDate1 = e1.getEventDate();
                            LocalDateTime eventDate2 = e2.getEventDate();
                            return eventDate1.compareTo(eventDate2);
                        })
                        .collect(Collectors.toList());
            }
        }
        RequestDto requestDto = RequestDto.builder()
                .app("ewm-main-service")
                .ip(remoteAddr)
                .uri(requestURI)
                .build();
        statClient.create(requestDto);
        return events.subList(from, events.size())
                .stream()
                .limit(size)
                .map(EventMapper::mapToEventShortDtoFromEvent)
                .peek(eventShortDto -> eventShortDto.setComments(
                        commentRepository.findAllByEvent_Id(eventShortDto.getId()).stream()
                                .map(CommentMapper::mapToCommentDtoFromComment)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto publicGetEventById(int id, String remoteAddr, String requestURI) {
        Event event = getById(id);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", id));
        }
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);
        RequestDto requestDto = RequestDto.builder()
                .app("ewm-main-service")
                .ip(remoteAddr)
                .uri(requestURI)
                .build();
        statClient.create(requestDto);
        EventFullDto outputEvent = EventMapper.mapToEventFullDtoFromEvent(event);
        outputEvent.setComments(commentRepository.findAllByEvent_Id(event.getId())
                .stream()
                .map(CommentMapper::mapToCommentDtoFromComment)
                .collect(Collectors.toList()));
        return outputEvent;
    }

    @Override
    public CommentDto privateAddComment(int userId, int eventId, NewCommentDto newCommentDto) {
        Event event = getById(eventId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%s was not found.", userId)));
        if (event.getInitiator().getId() == user.getId()) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s is initiator event with id=%s", userId, eventId));
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException(String.format("Event with id=%s not found", eventId));
        }
        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("You can't leave comments about an event that didn't happen");
        }
        Request request = requestRepository.findByRequester_IdAndEvent_Id(userId, eventId);
        if (request == null || !request.getStatus().equals(RequestState.CONFIRMED)) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s did not participate in the event with id=%s", userId, eventId));
        }
        Comment comment = CommentMapper.mapToCommentFromNewCommentDto(newCommentDto, user, event);
        return CommentMapper.mapToCommentDtoFromComment(commentRepository.save(comment));
    }

    @Override
    public CommentDto privateUpdateComment(int userId, int eventId, UpdateCommentDto updateCommentDto) {
        Comment comment = commentRepository.findById(updateCommentDto.getId())
                .orElseThrow(() -> new CommentNotFoundException(String
                        .format("Comment with id=%s not found", updateCommentDto.getId())));
        if (comment.getCommenter().getId() != userId) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not have access for comment with id=%s",
                            userId,
                            updateCommentDto.getId()));
        }
        comment.setText(updateCommentDto.getText());
        return CommentMapper.mapToCommentDtoFromComment(commentRepository.save(comment));
    }

    @Override
    public void privateDeleteCommentById(int userId, int eventId, int commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(String
                        .format("Comment with id=%s not found", commentId)));
        if (comment.getCommenter().getId() != userId) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not have access for comment with id=%s",
                            userId,
                            commentId));
        }
        commentRepository.deleteById(commentId);
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }

    private void checkingInitiatorAccess(int userId, Event event) {
        if (userId != event.getInitiator().getId()) {
            throw new IllegalArgumentException(String
                    .format("User with id=%s not access to event with id=%s", userId, event.getId()));
        }
    }

    private void checkingEventDate(LocalDateTime eventDate, String access) {
        if (access.equalsIgnoreCase("admin")) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1).plusSeconds(1))) {
                throw new IllegalArgumentException("Event date too early");
            }
        } else if (access.equalsIgnoreCase("private")) {
            if (eventDate.isBefore(LocalDateTime.now().plusHours(2).plusSeconds(1))) {
                throw new IllegalArgumentException("Event date too early");
            }
        }
    }

    private Event getById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(String.format("Event with id=%s was not found.", id)));
    }
}
