package ru.yandex.practicum.main.event.mapper;

import ru.yandex.practicum.main.category.model.Category;
import ru.yandex.practicum.main.event.dto.EventFullDto;
import ru.yandex.practicum.main.event.dto.EventShortDto;
import ru.yandex.practicum.main.event.dto.NewEventDto;
import ru.yandex.practicum.main.event.model.Comment;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.model.EventState;
import ru.yandex.practicum.main.event.model.Location;
import ru.yandex.practicum.main.user.mapper.UserMapper;
import ru.yandex.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    public static EventShortDto mapToEventShortDtoFromEvent(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.mapToUserShortDtoFromUser(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventFullDto mapToEventFullDtoFromEvent(Event event) {
        Location location = new Location(event.getLat(), event.getLon());
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .createdOn(event.getCreatedOn())
                .location(location)
                .state(event.getState())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .participantLimit(event.getParticipantLimit())
                .title(event.getTitle())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .paid(event.isPaid())
                .initiator(UserMapper.mapToUserShortDtoFromUser(event.getInitiator()))
                .views(event.getViews())
                .build();
    }

    public static Event mapToEventFromNewEventDto(NewEventDto newEventDto, User owner, Category category) {
        return new Event(0,
                newEventDto.getAnnotation(),
                category,
                0,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                owner,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon(),
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.isRequestModeration(),
                EventState.PENDING,
                newEventDto.getTitle(),
                0);
    }
}
