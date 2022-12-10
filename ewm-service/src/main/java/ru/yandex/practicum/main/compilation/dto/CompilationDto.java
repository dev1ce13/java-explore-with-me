package ru.yandex.practicum.main.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.main.event.dto.EventShortDto;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private int id;
    private Set<EventShortDto> events;
    private boolean pinned;
    private String title;
}
