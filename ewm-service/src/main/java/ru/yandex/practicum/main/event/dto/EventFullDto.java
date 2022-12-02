package ru.yandex.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.main.category.model.Category;
import ru.yandex.practicum.main.event.model.EventState;
import ru.yandex.practicum.main.event.model.Location;
import ru.yandex.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class EventFullDto {
    private int id;
    private String annotation;
    private Category category;
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private String title;
    private int views;
}
