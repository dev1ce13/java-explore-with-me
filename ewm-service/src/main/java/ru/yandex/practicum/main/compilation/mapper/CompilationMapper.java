package ru.yandex.practicum.main.compilation.mapper;

import ru.yandex.practicum.main.compilation.dto.CompilationDto;
import ru.yandex.practicum.main.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.main.compilation.model.Compilation;
import ru.yandex.practicum.main.event.mapper.EventMapper;
import ru.yandex.practicum.main.event.model.Event;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::mapToEventShortDtoFromEvent)
                        .collect(Collectors.toSet()))
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation mapToCompilationFromNewCompilationDto(NewCompilationDto newCompilationDto,
                                                                    List<Event> events) {
        return new Compilation(0,
                events,
                newCompilationDto.isPinned(),
                newCompilationDto.getTitle());
    }
}
