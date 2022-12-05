package ru.yandex.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.main.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    @Query(value = "SELECT r.* " +
            "FROM requests r " +
            "JOIN events e ON r.event_id = e.id " +
            "WHERE r.event_id = ?1 " +
            "AND e.initiator_id = ?2",
            nativeQuery = true)
    List<Request> findAllByEvent_IdAndInitiatorId(int eventId, int userId);

    List<Request> findAllByRequester_Id(int userId);

    Request findByRequester_IdAndEvent_Id(int userId, int eventId);
}
