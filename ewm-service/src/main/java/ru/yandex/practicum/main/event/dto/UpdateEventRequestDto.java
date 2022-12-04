package ru.yandex.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class UpdateEventRequestDto {
    @NotBlank
    private String annotation;
    @NotNull
    private int category;
    private String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull
    private int eventId;
    @NotNull
    private boolean paid;
    @NotNull
    private int participantLimit;
    @NotBlank
    private String title;
}
