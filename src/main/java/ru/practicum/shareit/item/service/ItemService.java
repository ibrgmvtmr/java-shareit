package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto getItem(Long id);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(ItemDto ItemDto, Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> search(String text);

}
