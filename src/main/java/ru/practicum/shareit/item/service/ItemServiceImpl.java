package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mappers.BookingMappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentAnswerDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMappers;
import ru.practicum.shareit.item.mapper.ItemMappers;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getItems(long userId, Integer from, Integer size) {
        if (Objects.isNull(from) || Objects.isNull(size)) {
            return Collections.emptyList();
        }

        if (from < 0 || size <= 0) {
            throw new BadPageArgumentException("такой страницы не существует");
        }
        PageRequest pageRequest = PageRequest.of(from > 0 ? from / size : 0, size, Sort.Direction.ASC, "id");

        Page<Item> items = itemRepository.findItemsByOwnerId(userId, pageRequest);
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = ItemMappers.toItemDto(item);

            BookingShortDto lastBooking = getLastBooking(userId);
            BookingShortDto nextBooking = getNextBooking(userId);
            List<CommentAnswerDto> comments = getComments(item.getId());

            if (lastBooking != null && item.getId().equals(lastBooking.getItemId())) {
                itemDto.setLastBooking(lastBooking);
            }

            if (nextBooking != null && item.getId().equals(nextBooking.getItemId())) {
                itemDto.setNextBooking(nextBooking);
            }
            itemDto.setComments(comments);

            itemDtos.add(itemDto);
        }

        return itemDtos;
    }

    private List<CommentAnswerDto> getComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMappers::toAnswerDto)
                .collect(Collectors.toList());
    }

    private BookingShortDto getLastBooking(long userId) {
        return BookingMappers.toBookingShortDto(bookingRepository
                .findFirstByItemOwnerIdAndStartIsBeforeAndStatus(userId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start")));
    }

    private BookingShortDto getNextBooking(long userId) {
        return BookingMappers.toBookingShortDto(bookingRepository
                .findFirstByItemOwnerIdAndStartIsAfterAndStatus(userId, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start")));
    }


    @Override
    public ItemDto getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        ItemDto itemDto = ItemMappers.toItemDto(item);

        BookingShortDto lastBooking = getLastBooking(userId);
        BookingShortDto nextBooking = getNextBooking(userId);
        List<CommentAnswerDto> comments = getComments(itemId);

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments);

        return itemDto;
    }


    @Override
    public ItemDto createItem(@Valid ItemDto itemDto, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        itemDto.setOwnerId(userId);

        return ItemMappers.toItemDto(itemRepository.save(ItemMappers.toItem(itemDto)));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId) {
        Item item = ItemMappers.toItem(itemDto);

        validation(userId, item.getId());

        Item resultItem = ItemMappers.toItem(getItem(item.getId(), userId));
        if (Objects.nonNull(item.getName())) {
            resultItem.setName(item.getName());
        }

        if (Objects.nonNull(item.getDescription())) {
            resultItem.setDescription(item.getDescription());
        }

        if (Objects.nonNull(item.getAvailable())) {
            resultItem.setAvailable(item.getAvailable());
        }

        return ItemMappers.toItemDto(itemRepository.save(resultItem));
    }

    private void validation(Long userId, Long itemId) {
        if (!Objects.equals(userId, itemRepository.findById(itemId).orElseThrow().getOwner().getId())) {
            throw new AccessDenideException("Пльзователю запрещено менять этот item");
        }
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        if (from < 0 || size <= 0) {
            throw new BadPageArgumentException("такой страницы не существует");
        }

        List<Item> items = itemRepository.searchByText(text, PageRequest.of(from > 0 ? from / size : 0, size, Sort.Direction.ASC, "id")).getContent();

        return items.stream().filter(e -> e.getAvailable().equals(true)).map(ItemMappers::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentAnswerDto postItemComment(@Valid CommentDto commentDto) {
        Booking booking = bookingRepository.findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(commentDto.getId(), commentDto.getItemId(), BookingStatus.APPROVED).orElseThrow(() -> new OwnerHasNotItemException("Пользователь не бронировал данную вещь"));

        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new TimeException("Пользователь не имеет права ставить отзывы до окончания аренды");
        }

        Comment comment = commentRepository.save(CommentMappers.toComment(commentDto));
        comment.setAuthor(userRepository.findById(comment.getAuthor().getId()).get());
        return CommentMappers.toAnswerDto(comment);
    }

}