package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
public class CommentDto {
    @NotNull
    private long id;
    @NotNull
    private long itemId;
    @NotBlank
    private String text;
}