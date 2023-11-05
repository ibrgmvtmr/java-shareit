package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadPageArgumentException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mappers.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    @Override
    public RequestDto get(long userId, int id) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return ItemRequestMapper.toRequestDto(itemRequestRepository.findById(Integer.toUnsignedLong(id)).orElseThrow(() -> new NotFoundException("Бранирование с таким айди не найдено")));
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto) {
        userRepository.findById(itemRequestDto.getRequester()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequest> getAll(long userId, Integer from, Integer size) {
        if (Objects.isNull(from) || Objects.isNull(size)) {
            return Collections.emptyList();
        }

        if (from < 0 || size <= 0) {
            throw new BadPageArgumentException("такой страницы не существует");
        }

        return itemRequestRepository.findAllByRequesterIdNot(userId, PageRequest.of(from > 0 ? from / size : 0, size, Sort.Direction.ASC, "id")).getContent();
    }

    @Override
    public List<ItemRequest> getAll(long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return itemRequestRepository.findAllByRequesterId(userId);
    }
}
