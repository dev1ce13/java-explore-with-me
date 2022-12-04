package ru.yandex.practicum.main.event.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.event.dto.AdminUpdateEventRequestDto;
import ru.yandex.practicum.main.event.dto.EventFullDto;
import ru.yandex.practicum.main.event.model.EventState;
import ru.yandex.practicum.main.event.service.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService service;

    @GetMapping
    public List<EventFullDto> adminGetEvents(
            @RequestParam(name = "users", required = false) List<Integer> users,
            @RequestParam(name = "states", required = false) List<String> states,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("/GET events admin");
        List<EventState> eventStates = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                EventState eventState = EventState.from(state)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
                eventStates.add(eventState);
            }
        }
        return service.adminGetEvents(users, eventStates, categories, rangeStart, rangeEnd, from, size);
    }

    @PutMapping("/{eventId}")
    public EventFullDto adminUpdateEvent(
            @PathVariable(name = "eventId") int id,
            @RequestBody AdminUpdateEventRequestDto adminUpdateEventRequestDto
    ) {
        log.info("/PUT event with id={} admin", id);
        return service.adminUpdateEvent(id, adminUpdateEventRequestDto);
    }

    @PatchMapping("/{eventId}/publish")
    public EventFullDto adminPublishEvent(@PathVariable(name = "eventId") int id) {
        log.info("/PATCH publish event with id={}", id);
        return service.adminPublishEvent(id);
    }

    @PatchMapping("/{eventId}/reject")
    public EventFullDto adminRejectEvent(@PathVariable(name = "eventId") int id) {
        log.info("/PATCH reject event with id={}", id);
        return service.adminRejectEvent(id);
    }
}
