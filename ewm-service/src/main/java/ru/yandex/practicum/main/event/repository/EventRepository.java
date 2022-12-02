package ru.yandex.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.event.model.EventState;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findAllByInitiator_Id(int id);

    List<Event> findAllByAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndState(String annotation,
                                                                                                 String description,
                                                                                                 EventState state);
}
