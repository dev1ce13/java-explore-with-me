package ru.yandex.practicum.stat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.stat.dto.EndpointHitDto;
import ru.yandex.practicum.stat.dto.NewEndpointHitDto;
import ru.yandex.practicum.stat.dto.ViewStats;
import ru.yandex.practicum.stat.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public EndpointHitDto addHit(
            @RequestBody NewEndpointHitDto newEndpointHitDto
    ) {
        log.info("/POST add hit");
        return statService.addHit(newEndpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(
            @RequestParam(name = "start")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") boolean unique
    ) {
        log.info("/GET stats");
        return statService.getStats(start, end, uris, unique);
    }
}
