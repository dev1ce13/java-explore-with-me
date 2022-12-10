package ru.yandex.practicum.main.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.yandex.practicum.main.request.model.RequestState;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class ParticipationRequestDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private int event;
    private int id;
    private int requester;
    private RequestState status;
}
