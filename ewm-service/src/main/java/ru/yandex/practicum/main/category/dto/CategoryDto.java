package ru.yandex.practicum.main.category.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class CategoryDto {
    @NotNull
    private int id;
    @NotBlank
    private String name;
}
