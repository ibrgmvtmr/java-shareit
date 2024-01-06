package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentAnswerDto {
    private long id;
    private String authorName;
    private String text;
    private LocalDateTime created;
}