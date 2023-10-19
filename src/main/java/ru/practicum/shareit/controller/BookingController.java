package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mappers.BookingMappers;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingShortDto bookingShortDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.create(bookingShortDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@PathVariable long bookingId, @RequestParam String approved, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.updateBookingStatus(BookingMappers.toBookingWithStateDto(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(BookingMappers.toBookingWithStateDto(bookingId, null, userId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingForUser(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllUserBookings(BookingMappers.toBookingWithStateDto(0, state, userId));
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingForOwnerItem(@RequestParam(defaultValue = "ALL") String state, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllOwnerItemBookings(BookingMappers.toBookingWithStateDto(0, state, userId));
    }
}