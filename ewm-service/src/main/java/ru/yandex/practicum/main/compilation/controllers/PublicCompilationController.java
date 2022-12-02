package ru.yandex.practicum.main.compilation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.compilation.dto.CompilationDto;
import ru.yandex.practicum.main.compilation.service.CompilationService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final CompilationService service;

    @GetMapping
    public List<CompilationDto> publicGetCompilations(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("/GET compilations");
        return service.publicGetCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto publicGetCompilationById(
            @PathVariable(name = "compId") int id
    ) {
        log.info("/GET compilation by id={}", id);
        return service.publicGetCompilationById(id);
    }
}
