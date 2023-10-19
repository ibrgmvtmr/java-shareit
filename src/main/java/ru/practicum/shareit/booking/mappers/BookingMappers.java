package ru.practicum.shareit.booking.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingWithStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@UtilityClass
public class BookingMappers {
    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(User.builder().id(booking.getBookerId()).build())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static Booking fromDto(BookingShortDto bookingShortDto) {
        return Booking.builder()
                .item(Item.builder().id(bookingShortDto.getItemId()).build())
                .status(BookingStatus.WAITING)
                .start(bookingShortDto.getStart())
                .end(bookingShortDto.getEnd())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (Objects.isNull(booking)) {
            return null;
        }
        return BookingShortDto.builder()
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBookerId())
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();

    }

    public static BookingWithStateDto toBookingWithStateDto(long id, String status, long userId) {
        return BookingWithStateDto.builder()
                .id(id)
                .state(status)
                .userId(userId)
                .build();
    }
}
