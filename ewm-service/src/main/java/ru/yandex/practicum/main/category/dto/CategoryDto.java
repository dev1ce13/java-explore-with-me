package ru.yandex.practicum.main.category.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CategoryDto {
    private int id;
    @NotNull
    @NotEmpty
    private String name;
}
