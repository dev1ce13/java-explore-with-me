package ru.yandex.practicum.main.category.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.category.dto.CategoryDto;
import ru.yandex.practicum.main.category.dto.NewCategoryDto;
import ru.yandex.practicum.main.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService service;

    @PatchMapping
    public CategoryDto adminUpdateCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("/PATCH category");
        return service.adminUpdateCategory(categoryDto);
    }

    @PostMapping
    public CategoryDto adminAddCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("/POST category");
        return service.adminAddCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    public void adminDeleteCategory(@PathVariable(name = "catId") int id) {
        log.info("/DELETE category with id={}", id);
        service.adminDeleteCategory(id);
    }
}
