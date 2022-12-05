package ru.yandex.practicum.main.compilation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.compilation.dto.CompilationDto;
import ru.yandex.practicum.main.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.main.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService service;

    @PostMapping
    public CompilationDto adminAddCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("/POST admin add compilation");
        return service.adminAddCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    public void adminDeleteCompilation(@PathVariable(name = "compId") int id) {
        log.info("/DELETE admin compilation with id={}", id);
        service.adminDeleteCompilation(id);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void adminDeleteEventFromCompilation(
            @PathVariable(name = "compId") int compId,
            @PathVariable(name = "eventId") int eventId
    ) {
        log.info("/DELETE admin event with id={} from compilation with id={}", eventId, compId);
        service.adminDeleteEventFromCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void adminAddEventFromCompilation(
            @PathVariable(name = "compId") int compId,
            @PathVariable(name = "eventId") int eventId
    ) {
        log.info("/PATCH admin event with id={} in compilation with id={}", eventId, compId);
        service.adminAddEventFromCompilation(compId, eventId);
    }

    @DeleteMapping("/{compId}/pin")
    public void adminUnpinCompilation(@PathVariable(name = "compId") int id) {
        log.info("/DELETE admin unpin compilation with id={}", id);
        service.adminUnpinCompilation(id);
    }

    @PatchMapping("/{compId}/pin")
    public void adminPinCompilation(@PathVariable(name = "compId") int id) {
        log.info("/PATCH admin pin compilation with id={}", id);
        service.adminPinCompilation(id);
    }
}
