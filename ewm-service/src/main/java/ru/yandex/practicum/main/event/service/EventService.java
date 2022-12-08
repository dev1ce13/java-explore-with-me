package ru.yandex.practicum.main.event.service;

import ru.yandex.practicum.main.event.dto.*;
import ru.yandex.practicum.main.event.model.EventSort;
import ru.yandex.practicum.main.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> adminGetEvents(List<Integer> users,
                                      List<EventState> states,
                                      List<Integer> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      int from,
                                      int size);

    EventFullDto adminUpdateEvent(int id, AdminUpdateEventRequestDto adminUpdateEventRequestDto);

    EventFullDto adminPublishEvent(int id);

    EventFullDto adminRejectEvent(int id);

    List<EventShortDto> privateGetEvents(int id, int from, int size);

    EventFullDto privateUpdateEvent(int id, UpdateEventRequestDto updateEventRequestDto);

    EventFullDto privateCreateEvent(int id, NewEventDto newEventDto);

    EventFullDto privateGetEventById(int userId, int eventId);

    EventFullDto privateRejectEventById(int userId, int eventId);

    List<EventShortDto> publicGetEvents(String text,
                                        List<Integer> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable,
                                        EventSort eventSort,
                                        int from,
                                        int size,
                                        String remoteAddr,
                                        String requestURI);

    EventFullDto publicGetEventById(int id, String remoteAddr, String requestURI);

    CommentDto privateAddComment(int userId, int eventId, NewCommentDto newCommentDto);

    CommentDto privateUpdateComment(int userId, int eventId, UpdateCommentDto updateCommentDto);

    void privateDeleteCommentById(int userId, int eventId, int commentId);
}
