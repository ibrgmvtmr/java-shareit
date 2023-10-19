package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mappers.BookingMappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.AccessDenideException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.OwnerHasNotItemException;
import ru.practicum.shareit.exceptions.TimeException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findItemsByOwnerId(userId).stream()
                .peek(item -> {
                    setLastFutureBooking(item, userId);
                    setItemComments(item);
                })
                .map(ItemMappers::toItemDto)
                .collect(Collectors.toList());
    }

    private void setItemComments(Item item) {
        List<CommentAnswerDto> comments = commentRepository.findAllByItemId(item.getId()).stream().map(CommentMappers::toAnswerDto).collect(Collectors.toList());
        item.setComments(comments);
    }

    private void setLastFutureBooking(Item item, long userId) {
        BookingShortDto lastBooking = BookingMappers.toBookingShortDto(bookingRepository.findFirstByItemOwnerIdAndStartIsBeforeAndStatusOrderByStartDesc(userId, LocalDateTime.now(), BookingStatus.APPROVED));
        BookingShortDto futureBooking = BookingMappers.toBookingShortDto(bookingRepository.findFirstByItemOwnerIdAndStartIsAfterAndStatusOrderByStartAsc(userId, LocalDateTime.now(), BookingStatus.APPROVED));
        if (Objects.nonNull(lastBooking)) {
            if (item.getId().equals(lastBooking.getItemId())) {
                item.setLastBooking(lastBooking);
            }
        }
        if (Objects.nonNull(futureBooking)) {
            if (item.getId().equals(futureBooking.getItemId())) {
                item.setNextBooking(futureBooking);
            }
        }
    }

    @Override
    public ItemDto getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с таким id не найдена"));
        setItemComments(item);
        setLastFutureBooking(item, userId);
        return ItemMappers.toItemDto(item);
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
        if (!Objects.equals(userId, itemRepository.findById(itemId).orElseThrow().getOwnerId())) {
            throw new AccessDenideException("Пльзователю запрещено менять этот item");
        }
    }

    @Override
    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMappers::toItemDto)
                .collect(Collectors.toList());
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
