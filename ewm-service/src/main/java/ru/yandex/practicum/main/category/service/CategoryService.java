package ru.yandex.practicum.main.category.service;

import ru.yandex.practicum.main.category.dto.CategoryDto;
import ru.yandex.practicum.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto adminUpdateCategory(CategoryDto categoryDto);

    CategoryDto adminAddCategory(NewCategoryDto newCategoryDto);

    void adminDeleteCategory(int id);

    List<CategoryDto> publicGetCategories(int from, int size);

    CategoryDto publicGetCategoryById(int id);
}
