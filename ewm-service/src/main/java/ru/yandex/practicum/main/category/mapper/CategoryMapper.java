package ru.yandex.practicum.main.category.mapper;

import ru.yandex.practicum.main.category.dto.CategoryDto;
import ru.yandex.practicum.main.category.dto.NewCategoryDto;
import ru.yandex.practicum.main.category.model.Category;

public class CategoryMapper {

    public static CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category mapToCategoryFromNewCategoryDto(NewCategoryDto newCategoryDto) {
        return new Category(0, newCategoryDto.getName());
    }
}
