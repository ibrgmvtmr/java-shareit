package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotNull
    private long id;
    @NotNull
    private long itemId;
    @NotBlank
    private String text;
}