package ru.yandex.practicum.main.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.main.compilation.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Integer> {

    List<Compilation> findAllByPinnedIs(Boolean pinned);
}
