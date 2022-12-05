package ru.yandex.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.main.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findAllByInitiator_Id(int id);

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "WHERE (upper(annotation) like upper(?1) " +
            "or upper(description) like upper(?2)) " +
            "and state = ?3",
            nativeQuery = true)
    List<Event> findAllForPublic(String annotation, String description, String state);
}
