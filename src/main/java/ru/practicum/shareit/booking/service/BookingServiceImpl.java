package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingWithStateDto;
import ru.practicum.shareit.booking.mappers.BookingMappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    public List<BookingDto> getAllUserBookings(BookingWithStateDto bookingWithStateDto) {

        long userId = bookingWithStateDto.getUserId();
        String state = bookingWithStateDto.getState();
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
        if (state.equals("ALL")) {
            return bookingRepository.findAllByBookerIdOrderByIdDesc(userId).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
        }
        if (state.equals("CURRENT")) {
            return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now()).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            return bookingRepository.findAllByBookerIdAndStatusAndStartIsBeforeAndEndIsBeforeOrderByIdDesc(userId, BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now()).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
        }
        throw new IncorrectStateException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingDto> getAllOwnerItemBookings(BookingWithStateDto bookingWithStateDto) {
        long userId = bookingWithStateDto.getUserId();
        String state = bookingWithStateDto.getState();
        int zeroItem = 0;
        long countItemUser = itemRepository.countItemByOwnerId(userId);
        if (countItemUser > zeroItem) {
            if (state.equals("ALL")) {
                return bookingRepository.findAllByItemOwnerIdOrderByIdDesc(userId).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
            }
            if (state.equals("FUTURE")) {
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now()).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
            }
            if (state.equals("WAITING")) {
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
            }
            if (state.equals("REJECTED")) {
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
            }
            if (state.equals("CURRENT")) {
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now()).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
            }
            if (state.equals("PAST")) {
                return bookingRepository.findAllByItemOwnerIdAndStatusAndStartIsBeforeAndEndIsBeforeOrderByIdDesc(userId, BookingStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now()).stream().map(BookingMappers::toBookingDto).collect(Collectors.toList());
            }
        }
        throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public BookingDto getBooking(BookingWithStateDto bookingWithStateDto) {
        long bookingId = bookingWithStateDto.getId();
        long userId = bookingWithStateDto.getUserId();
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бранирование не найдено"));

        validate(booking, userId);
        return BookingMappers.toBookingDto(booking);
    }

    @Override
    public BookingDto create(@Valid BookingShortDto bookingShortDto, long userId) {
        Booking booking = BookingMappers.fromDto(bookingShortDto);
        booking.setBookerId(userId);
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isBefore(LocalDateTime.now()) || booking.getStart().equals(booking.getEnd())) {
            throw new BookingTimeException("Неверное время бронирования");
        }
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (booking.getBookerId() == item.getOwnerId()) {
            throw new OwnerHasNoRightsException("Владелец не может бронировать свою вещь");
        }
        if (item.getAvailable().equals(false)) {
            throw new ItemUnavailableException("Вещь недоступна");
        }
        userRepository.findById(booking.getBookerId()).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Booking savedBooking = bookingRepository.save(booking);
        savedBooking.setItem(item);
        return BookingMappers.toBookingDto(savedBooking);
    }

    public BookingDto updateBookingStatus(BookingWithStateDto bookingWithStateDto) {
        Booking booking = bookingRepository.findById(bookingWithStateDto.getId()).orElseThrow(() -> new NotFoundException("Бранирование не найдено"));
        String status = bookingWithStateDto.getState();
        long userId = bookingWithStateDto.getUserId();

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ItemUnavailableException("После подтверждения изменения в обратную сторону запрещены");
        }

        if (status.equals("true")) {
            if (Objects.equals(booking.getItem().getOwnerId(), userId)) {
                booking.setStatus(BookingStatus.APPROVED);
                bookingRepository.save(booking);
                return BookingMappers.toBookingDto(booking);
            } else {
                throw new NotFoundException("Пользователь запросивший изменение не является владельцем вещи");
            }
        }

        if (status.equals("false")) {
            if (Objects.equals(booking.getItem().getOwnerId(), userId)) {
                booking.setStatus(BookingStatus.REJECTED);
            } else if (Objects.equals(booking.getBookerId(), userId)) {
                booking.setStatus(BookingStatus.CANCELED);
            } else {
                throw new NotFoundException("Пользователь запросивший изменение не является владельцем вещи");
            }
            bookingRepository.save(booking);
            return BookingMappers.toBookingDto(booking);
        }
        throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public void delete(int id) {

    }

    private void validate(Booking booking, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (Objects.equals(booking.getBookerId(), userId) || Objects.equals(booking.getItem().getOwnerId(), userId)) {
            return;
        }
        throw new NotFoundException("Нет доступа для просмотра");
    }
}
