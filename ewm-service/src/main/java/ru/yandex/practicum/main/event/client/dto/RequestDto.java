package ru.yandex.practicum.main.event.client.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDto {
    private String app;
    private String uri;
    private String ip;
}
