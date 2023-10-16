package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMappers {

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .author(User.builder().id(commentDto.getId()).build())
                .text(commentDto.getText())
                .itemId(commentDto.getItemId())
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentAnswerDto toAnswerDto(Comment comment) {
        return CommentAnswerDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .build();
    }
}
