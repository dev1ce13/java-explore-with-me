package ru.yandex.practicum.main.event.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.event.dto.EventFullDto;
import ru.yandex.practicum.main.event.dto.EventShortDto;
import ru.yandex.practicum.main.event.model.EventSort;
import ru.yandex.practicum.main.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class PublicController {

    private final EventService service;

    @GetMapping
    public List<EventShortDto> publicGetEvents(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Integer> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        log.info("/GET events");
        EventSort eventSort = null;
        if (sort != null) {
            eventSort = EventSort.from(sort)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown sort: " + sort));
        }
        return service.publicGetEvents(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                eventSort,
                from,
                size,
                request.getRemoteAddr(),
                request.getRequestURI());
    }

    @GetMapping("/{id}")
    public EventFullDto publicGetEventById(
            @PathVariable int id,
            HttpServletRequest request
    ) {
        log.info("/GET event by id={}", id);
        return service.publicGetEventById(id, request.getRemoteAddr(), request.getRequestURI());
    }
}
