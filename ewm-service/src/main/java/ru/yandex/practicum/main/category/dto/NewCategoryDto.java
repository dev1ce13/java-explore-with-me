package ru.yandex.practicum.main.category.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class NewCategoryDto {
    @NotNull
    @NotEmpty
    private String name;
}
