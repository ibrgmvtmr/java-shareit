package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.AccessDenideException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemRepository.getItems().stream()
                .filter(e -> Objects.equals(e.getOwner(), userId))
                .map(ItemMappers::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItem(Long id) {
        checkItemId(id);
        return ItemMappers.toItemDto(itemRepository.getItem(id));
    }


    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (Objects.isNull(userRepository.getUser(userId))) {
            throw new NotFoundException("пользователь с таким id не найден");
        }
        Item item = ItemMappers.toItem(itemDto);
        item.setOwner(userId);
        Item createdItem = itemRepository.createItem(item);
        return ItemMappers.toItemDto(createdItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long userId) {
        validation(userId, itemDto.getId());
        Item item = ItemMappers.toItem(itemDto);

        Item item1 = itemRepository.getItem(itemDto.getId());
        if (Objects.nonNull(itemDto.getName())) {
            item1.setName(itemDto.getName());
        }
        if (Objects.nonNull(itemDto.getDescription())) {
            item1.setDescription(itemDto.getDescription());
        }
        if (Objects.nonNull(itemDto.getAvailable())) {
            item1.setIsAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.updateItem(item1);
        return ItemMappers.toItemDto(updatedItem);
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
