package ru.yandex.practicum.main.event.mapper;

import ru.yandex.practicum.main.event.dto.CommentDto;
import ru.yandex.practicum.main.event.dto.NewCommentDto;
import ru.yandex.practicum.main.event.model.Comment;
import ru.yandex.practicum.main.event.model.Event;
import ru.yandex.practicum.main.user.mapper.UserMapper;
import ru.yandex.practicum.main.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment mapToCommentFromNewCommentDto(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(0, newCommentDto.getText(), LocalDateTime.now(), user, event);
    }

    public static CommentDto mapToCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commenter(UserMapper.mapToUserShortDtoFromUser(comment.getCommenter()))
                .eventId(comment.getEvent().getId())
                .timestamp(comment.getTimestamp())
                .build();
    }
}
