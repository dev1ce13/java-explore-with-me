package ru.yandex.practicum.stat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEndpointHitDto {
    private String app;
    private String uri;
    private String ip;
}
