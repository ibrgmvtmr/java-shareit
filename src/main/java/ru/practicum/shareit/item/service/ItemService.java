package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId, Integer from, Integer size);

    ItemDto getItem(long itemId, long userId);

    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId);

    void deleteItem(long itemId);

    List<ItemDto> search(String text, Integer from, Integer size);

    CommentAnswerDto postItemComment(CommentDto comment);

}
