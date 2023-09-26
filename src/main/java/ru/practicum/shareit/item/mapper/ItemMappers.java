package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMappers {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .available(item.getIsAvailable())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .isAvailable(itemDto.getAvailable())
                .id(itemDto.getId()).build();
    }
}
