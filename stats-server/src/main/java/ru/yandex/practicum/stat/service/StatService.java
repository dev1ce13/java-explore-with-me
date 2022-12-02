package ru.yandex.practicum.stat.service;

import ru.yandex.practicum.stat.dto.EndpointHitDto;
import ru.yandex.practicum.stat.dto.NewEndpointHitDto;
import ru.yandex.practicum.stat.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    EndpointHitDto addHit(NewEndpointHitDto newEndpointHitDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
