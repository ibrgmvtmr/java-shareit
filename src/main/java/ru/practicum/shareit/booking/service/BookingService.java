package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingWithStateDto;

import java.util.List;

public interface BookingService {
    void delete(int id);

    BookingDto getBooking(BookingWithStateDto id);

    BookingDto create(BookingShortDto bookingShortDto, long userId);

    BookingDto updateBookingStatus(BookingWithStateDto bookingWithStateDto);

    List<BookingDto> getAllUserBookings(BookingWithStateDto bookingWithStateDto);

    List<BookingDto> getAllOwnerItemBookings(BookingWithStateDto bookingWithStateDto);
}
