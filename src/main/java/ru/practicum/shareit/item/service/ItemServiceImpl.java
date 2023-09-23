package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDenideException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public List<Item> getItems(Long userId) {
        return itemRepository.getItems().stream().filter(e -> Objects.equals(e.getOwner(), userId)).collect(Collectors.toList());
    }

    @Override
    public Item getItem(Long id) {
        checkItemId(id);
        return itemRepository.getItem(id);
    }

    @Override
    public Item createItem(Item item, Long userId) {
        item.setOwner(userId);
        return itemRepository.createItem(item);
    }

    @Override
    public Item updateItem(Item item, Long userId) {
        validation(userId, item.getId());

        Item item1 = itemRepository.getItem(item.getId());
        if (Objects.nonNull(item.getName())) {
            item1.setName(item.getName());
        }
        if (Objects.nonNull(item.getDescription())) {
            item1.setDescription(item.getDescription());
        }
        if (Objects.nonNull(item.getIsAvailable())) {
            item1.setIsAvailable(item.getIsAvailable());
        }
        return itemRepository.updateItem(item1);
    }

    private void checkItemId(Long itemId) {
        if (itemRepository.getItem(itemId) == null) {
            throw new NotFoundException(String.format("Вещь с id %d не найден", itemId));
        }
    }

    private void validation(Long userId, Long itemId) {
        if (!Objects.equals(userId, itemRepository.getItem(itemId).getOwner())) {
            throw new AccessDenideException("Пльзователю запрещено менять этот item");
        }
    }

    @Override
    public void deleteItem(Long itemId) {
        checkItemId(itemId);
        itemRepository.deleteItem(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.getItems().stream()
                .filter(item -> item.getName().toLowerCase()
                        .contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getIsAvailable)
                .map(ItemMappers::toItemDto).collect(Collectors.toList());
    }

}
