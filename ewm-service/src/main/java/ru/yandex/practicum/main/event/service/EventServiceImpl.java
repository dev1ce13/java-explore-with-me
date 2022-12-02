package ru.yandex.practicum.main.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.main.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.main.category.model.Category;
import ru.yandex.practicum.main.category.repository.CategoryRepository;
import ru.yandex.practicum.main.event.client.StatClient;
import ru.yandex.practicum.main.event.client.dto.RequestDto;
import ru.yandex.practicum.main.event.dto.*;
import ru.yandex.practicum.main.event.exception.EventNotFoundException;
import ru.yandex.practicum.main.event.mapper.EventMapper;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.model.EventSort;
import ru.yandex.practicum.main.event.model.EventState;
import ru.yandex.practicum.main.event.repository.EventRepository;
import ru.yandex.practicum.main.user.exception.UserNotFoundException;
import ru.yandex.practicum.main.user.model.User;
import ru.yandex.practicum.main.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatClient statClient;

    @Override
    @Transactional
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
            Optional<Category> category = categoryRepository.findById(adminUpdateEventRequestDto.getCategory());
            if (category.isPresent()) {
                event.setCategory(category.get());
            } else {
                throw new CategoryNotFoundException(String
                        .format("Category with id=%s was not found.", adminUpdateEventRequestDto.getCategory()));
            }
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
        if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1).plusSeconds(1))) {
            event.setState(EventState.PUBLISHED);
            return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
        } else {
            throw new IllegalArgumentException("Event date too early");
        }
    }

    @Override
    @Transactional
    public EventFullDto adminRejectEvent(int id) {
        Event event = getById(id);
        event.setState(EventState.CANCELED);
        return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
    }

    @Override
    @Transactional
    public List<EventShortDto> privateGetEvents(int id, int from, int size) {
        List<Event> events = eventRepository.findAllByInitiator_Id(id);
        checkingFromParameter(from, events.size());
        return events.subList(from, events.size())
                .stream()
                .limit(size)
                .map(EventMapper::mapToEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto privateUpdateEvent(int id, UpdateEventRequestDto updateEventRequestDto) {
        Optional<Category> category = categoryRepository.findById(updateEventRequestDto.getCategory());
        Event event = getById(updateEventRequestDto.getEventId());
        if (event.getInitiator().getId() == id && category.isPresent()) {
            if (updateEventRequestDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2).plusSeconds(1))) {
                if (event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED)) {
                    event.setAnnotation(updateEventRequestDto.getAnnotation());
                    event.setCategory(category.get());
                    event.setDescription(updateEventRequestDto.getDescription());
                    event.setEventDate(updateEventRequestDto.getEventDate());
                    event.setPaid(updateEventRequestDto.isPaid());
                    event.setParticipantLimit(updateEventRequestDto.getParticipantLimit());
                    event.setTitle(updateEventRequestDto.getTitle());
                    event.setState(EventState.PENDING);
                    return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
                } else {
                    throw new IllegalArgumentException("The event has already been published");
                }
            } else {
                throw new IllegalArgumentException("Event date too early");
            }
        } else {
            throw new CategoryNotFoundException(String
                    .format("Category with id=%s was not found.", updateEventRequestDto.getCategory()));
        }
    }

    @Override
    @Transactional
    public EventFullDto privateCreateEvent(int id, NewEventDto newEventDto) {
        Optional<User> user = userRepository.findById(id);
        Optional<Category> category = categoryRepository.findById(newEventDto.getCategory());
        if (user.isEmpty()) {
            throw new UserNotFoundException(String
                    .format("User with id=%s was not found.", id));
        } else if (category.isEmpty()) {
            throw new CategoryNotFoundException(String
                    .format("Category with id=%s was not found.", newEventDto.getCategory()));
        } else {
            Event event = EventMapper.mapToEventFromNewEventDto(newEventDto, user.get(), category.get());
            return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
        }
    }

    @Override
    @Transactional
    public EventFullDto privateGetEventById(int userId, int eventId) {
        Event event = getById(eventId);
        if (event.getInitiator().getId() == userId) {
            return EventMapper.mapToEventFullDtoFromEvent(event);
        } else {
            throw new IllegalArgumentException(String
                    .format("User with id=%s don't look full info for event with id=%s", userId, event));
        }
    }

    @Override
    @Transactional
    public EventFullDto privateRejectEventById(int userId, int eventId) {
        Event event = getById(eventId);
        if (event.getInitiator().getId() == userId) {
            if (event.getState().equals(EventState.PENDING)) {
                event.setState(EventState.CANCELED);
                return EventMapper.mapToEventFullDtoFromEvent(eventRepository.save(event));
            } else {
                throw new IllegalArgumentException(String
                        .format("Event with id=%s cannot be canceled", eventId));
            }
        } else {
            throw new IllegalArgumentException(String
                    .format("User with id=%s don't reject event with id=%s", userId, event));
        }
    }

    @Override
    @Transactional
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
        List<Event> events = eventRepository.findAllByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndState
                (text, text, EventState.PUBLISHED);
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
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto publicGetEventById(int id, String remoteAddr, String requestURI) {
        Event event = getById(id);
        if (event.getState().equals(EventState.PUBLISHED)) {
            event.setViews(event.getViews() + 1);
            eventRepository.save(event);
            RequestDto requestDto = RequestDto.builder()
                    .app("ewm-main-service")
                    .ip(remoteAddr)
                    .uri(requestURI)
                    .build();
            statClient.create(requestDto);
            return EventMapper.mapToEventFullDtoFromEvent(event);
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", id));
        }
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }

    private Event getById(int id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            return event.get();
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", id));
        }
    }
}
