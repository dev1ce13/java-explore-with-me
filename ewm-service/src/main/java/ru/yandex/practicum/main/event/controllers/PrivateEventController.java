package ru.yandex.practicum.main.event.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.event.dto.EventFullDto;
import ru.yandex.practicum.main.event.dto.EventShortDto;
import ru.yandex.practicum.main.event.dto.NewEventDto;
import ru.yandex.practicum.main.event.dto.UpdateEventRequestDto;
import ru.yandex.practicum.main.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("users/{userId}/events")
public class PrivateEventController {

    private final EventService service;

    @GetMapping
    public List<EventShortDto> privateGetEvents(
            @PathVariable(name = "userId") int id,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("/GET user with id={} events", id);
        return service.privateGetEvents(id, from, size);
    }

    @PatchMapping
    public EventFullDto privateUpdateEvent(
            @PathVariable(name = "userId") int id,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto
    ) {
        log.info("/PATCH user with id={} update event with id={}", id, updateEventRequestDto.getEventId());
        return service.privateUpdateEvent(id, updateEventRequestDto);
    }

    @PostMapping
    public EventFullDto privateCreateEvent(
            @PathVariable(name = "userId") int id,
            @Valid @RequestBody NewEventDto newEventDto
    ) {
        log.info("/POST user with id={} create new event", id);
        return service.privateCreateEvent(id, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto privateGetEventById(
            @PathVariable(name = "userId") int id,
            @PathVariable(name = "eventId") int eventId
    ) {
        log.info("/GET user with id={} event with id={}", id, eventId);
        return service.privateGetEventById(id, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto privateRejectEventById(
            @PathVariable(name = "userId") int id,
            @PathVariable(name = "eventId") int eventId
    ) {
        log.info("/PATCH user with id={} reject event with id={}", id, eventId);
        return service.privateRejectEventById(id, eventId);
    }
}
