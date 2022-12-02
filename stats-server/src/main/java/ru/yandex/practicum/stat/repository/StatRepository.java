package ru.yandex.practicum.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.stat.dto.ViewStats;
import ru.yandex.practicum.stat.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Query(nativeQuery = true)
    List<ViewStats> getUniqueStat(LocalDateTime start, LocalDateTime end);

    @Query(nativeQuery = true)
    List<ViewStats> getNoUniqueStat(LocalDateTime start, LocalDateTime end);
}
