package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(Long userId);

    Item getItem(Long id);

    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> search(String text);

}
