package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
public class CommentAnswerDto {
    private long id;
    private String authorName;
    private String text;
    private LocalDateTime created;
}