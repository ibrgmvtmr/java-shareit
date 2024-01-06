package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class RequestDto {
    private long id;
    private long requester;
    private String description;
    private LocalDateTime created;
    private List<Item> items;
}
