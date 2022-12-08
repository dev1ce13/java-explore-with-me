package ru.yandex.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.main.event.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllByEvent_Id(int eventId);
}
