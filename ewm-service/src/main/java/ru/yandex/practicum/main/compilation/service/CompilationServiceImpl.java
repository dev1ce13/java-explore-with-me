package ru.yandex.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.main.compilation.dto.CompilationDto;
import ru.yandex.practicum.main.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.main.compilation.exception.CompilationNotFoundException;
import ru.yandex.practicum.main.compilation.mapper.CompilationMapper;
import ru.yandex.practicum.main.compilation.model.Compilation;
import ru.yandex.practicum.main.compilation.repository.CompilationRepository;
import ru.yandex.practicum.main.event.exception.EventNotFoundException;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.repository.EventRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto adminAddCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.mapToCompilationFromNewCompilationDto(newCompilationDto, events);
        return CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void adminDeleteCompilation(int id) {
        getById(id);
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void adminDeleteEventFromCompilation(int compId, int eventId) {
        Compilation compilation = getById(compId);
        List<Event> events = compilation.getEvents();
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            events.remove(event.get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
    }

    @Override
    @Transactional
    public void adminAddEventFromCompilation(int compId, int eventId) {
        Optional<Event> event = eventRepository.findById(eventId);
        Compilation compilation = getById(compId);
        if (event.isPresent()) {
            List<Event> events = compilation.getEvents();
            events.add(event.get());
            compilation.setEvents(events);
            compilationRepository.save(compilation);
        } else {
            throw new EventNotFoundException(String.format("Event with id=%s was not found.", eventId));
        }
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
    @Transactional
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
    @Transactional
    public CompilationDto publicGetCompilationById(int id) {
        Compilation compilation = getById(id);
        System.out.println("\n" + compilation + "\n");
        return CompilationMapper.mapToCompilationDto(compilation);
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }

    private Compilation getById(int id) {
        Optional<Compilation> compilation = compilationRepository.findById(id);
        if (compilation.isPresent()) {
            return compilation.get();
        } else {
            throw new CompilationNotFoundException(String
                    .format("Compilation with id=%s was not found.", id));
        }
    }
}
