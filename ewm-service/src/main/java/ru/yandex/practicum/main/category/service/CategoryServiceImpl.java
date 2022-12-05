package ru.yandex.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.main.category.dto.CategoryDto;
import ru.yandex.practicum.main.category.dto.NewCategoryDto;
import ru.yandex.practicum.main.category.exception.CategoryNotFoundException;
import ru.yandex.practicum.main.category.exception.DuplicateCategoryNameException;
import ru.yandex.practicum.main.category.mapper.CategoryMapper;
import ru.yandex.practicum.main.category.model.Category;
import ru.yandex.practicum.main.category.repository.CategoryRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto adminUpdateCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()) != null) {
            throw new DuplicateCategoryNameException("could not execute statement;",
                    new SQLException("nested exception is org.hibernate.exception. " +
                            "ConstraintViolationException: could not execute statement"),
                    "SQL [n/a];",
                    "constraint [uq_category_name];");
        }
        Category category = getById(categoryDto.getId());
        category.setName(categoryDto.getName());
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto adminAddCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.findByName(newCategoryDto.getName()) != null) {
            throw new DuplicateCategoryNameException("could not execute statement;",
                    new SQLException("nested exception is org.hibernate.exception. " +
                            "ConstraintViolationException: could not execute statement"),
                    "SQL [n/a];",
                    "constraint [uq_category_name];");
        }
        Category category = CategoryMapper.mapToCategoryFromNewCategoryDto(newCategoryDto);
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void adminDeleteCategory(int id) {
        getById(id);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> publicGetCategories(int from, int size) {
        List<Category> categories = categoryRepository.findAll();
        checkingFromParameter(from, categories.size());
        return categories.subList(from, categories.size())
                .stream()
                .limit(size)
                .map(CategoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto publicGetCategoryById(int id) {
        return CategoryMapper.mapToCategoryDto(getById(id));
    }

    private void checkingFromParameter(int from, int listSize) {
        if (from > listSize) {
            throw new IllegalArgumentException("Parameter from must be lower size list");
        }
    }

    private Category getById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(String
                        .format("Category with id=%s was not found.", id)));
    }
}
