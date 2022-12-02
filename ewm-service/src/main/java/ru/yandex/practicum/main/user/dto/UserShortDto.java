package ru.yandex.practicum.main.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDto {
    private int id;
    private String name;
}
