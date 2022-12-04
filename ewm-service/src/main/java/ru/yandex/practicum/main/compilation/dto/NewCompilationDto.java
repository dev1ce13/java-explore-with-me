package ru.yandex.practicum.main.compilation.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
@Builder
public class NewCompilationDto {
    private Set<Integer> events;
    private boolean pinned;
    @NotBlank
    private String title;
}
