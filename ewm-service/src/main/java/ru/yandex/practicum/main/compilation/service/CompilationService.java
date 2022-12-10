package ru.yandex.practicum.main.compilation.service;

import ru.yandex.practicum.main.compilation.dto.CompilationDto;
import ru.yandex.practicum.main.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto);

    void adminDeleteCompilation(int id);

    void adminDeleteEventFromCompilation(int compId, int eventId);

    void adminAddEventFromCompilation(int compId, int eventId);

    void adminUnpinCompilation(int id);

    void adminPinCompilation(int id);

    List<CompilationDto> publicGetCompilations(Boolean pinned, int from, int size);

    CompilationDto publicGetCompilationById(int id);
}
