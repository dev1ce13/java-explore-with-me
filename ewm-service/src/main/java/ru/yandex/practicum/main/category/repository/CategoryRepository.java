package ru.yandex.practicum.main.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.main.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category findByName(String name);
}
