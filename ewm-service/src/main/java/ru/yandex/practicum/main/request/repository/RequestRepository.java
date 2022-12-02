package ru.yandex.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.main.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findAllByEvent_Id(int eventId);

    List<Request> findAllByRequester_Id(int userId);
}
