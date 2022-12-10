package ru.yandex.practicum.stat.mapper;

import ru.yandex.practicum.stat.dto.EndpointHitDto;
import ru.yandex.practicum.stat.dto.NewEndpointHitDto;
import ru.yandex.practicum.stat.model.EndpointHit;

import java.time.LocalDateTime;

public class HitMapper {

    public static EndpointHit mapToEndpointHitFromNewEndpointHitDto(NewEndpointHitDto newEndpointHitDto) {
        return new EndpointHit(0,
                newEndpointHitDto.getApp(),
                newEndpointHitDto.getUri(),
                newEndpointHitDto.getIp(),
                LocalDateTime.now()
        );
    }

    public static EndpointHitDto mapToEndpointHitDtoFromEndpointHit(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .id(endpointHit.getId())
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}
