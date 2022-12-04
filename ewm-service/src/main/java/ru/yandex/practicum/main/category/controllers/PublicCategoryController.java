package ru.yandex.practicum.main.category.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.category.dto.CategoryDto;
import ru.yandex.practicum.main.category.service.CategoryService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/categories")
public class PublicCategoryController {

    private final CategoryService service;

    @GetMapping
    public List<CategoryDto> publicGetCategories(
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("/GET categories");
        return service.publicGetCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto publicGetCategoryById(@PathVariable(name = "catId") int id) {
        log.info("/GET category by id={}", id);
        return service.publicGetCategoryById(id);
    }
}
