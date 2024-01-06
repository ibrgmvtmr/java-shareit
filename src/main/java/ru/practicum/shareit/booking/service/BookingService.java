package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingWithStateDto;

import java.util.List;

public interface BookingService {

    BookingDto getBooking(BookingWithStateDto id);

    BookingDto create(BookingShortDto bookingShortDto, long userId);

    BookingDto updateBookingStatus(BookingWithStateDto bookingWithStateDto);

    List<BookingDto> getAllUserBookings(BookingWithStateDto bookingWithStateDto, Integer from, Integer size);

    List<BookingDto> getAllOwnerItemBookings(BookingWithStateDto bookingWithStateDto, Integer from, Integer size);
}
