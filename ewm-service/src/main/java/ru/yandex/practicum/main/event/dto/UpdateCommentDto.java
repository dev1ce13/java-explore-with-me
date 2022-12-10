package ru.yandex.practicum.main.event.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateCommentDto {
    @NotNull
    private int id;
    @NotBlank
    private String text;
}
