package ru.yandex.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.main.compilation.dto.CompilationDto;
import ru.yandex.practicum.main.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.main.compilation.exception.CompilationNotFoundException;
import ru.yandex.practicum.main.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.main.compilation.model.Compilation;
import ru.yandex.practicum.main.compilation.repository.CompilationRepository;
import ru.yandex.practicum.main.event.exception.EventNotFoundException;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        Compilation compilation = CompilationMapper.mapToCompilationFromNewCompilationDto(newCompilationDto, events);
        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void adminDeleteCompilation(int id) {
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void adminDeleteEventFromCompilation(int compId, int eventId) {
        Compilation compilation = getById(compId);
        Set<Event> events = compilation.getEvents();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        events.remove(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void adminAddEventFromCompilation(int compId, int eventId) {
        Compilation compilation = getById(compId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String
                        .format("Event with id=%s was not found.", eventId)));
        Set<Event> events = compilation.getEvents();
        events.add(event);
        compilation.setEvents(events);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void adminUnpinCompilation(int id) {
        Compilation compilation = getById(id);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional
    public void adminPinCompilation(int id) {
        Compilation compilation = getById(id);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> publicGetCompilations(Boolean pinned, int from, int size) {
        List<Compilation> compilations = compilationRepository.findAll();
        if (pinned != null) {
            compilations = compilations.stream()
                    .filter(compilation -> compilation.isPinned() == pinned)
                    .collect(Collectors.toList());
        }
        checkingFromParameter(from, compilations.size());
        return compilations.subList(from, compilations.size())
                .stream()
                .limit(size)
                .map(CompilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto publicGetCompilationById(int id) {
        return CompilationMapper.mapToCompilationDto(getById(id));
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }

    private Compilation getById(int id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new CompilationNotFoundException(String
                        .format("Compilation with id=%s was not found.", id)));
    }
}
