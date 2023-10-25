package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BookingWithStateDto {
    private long id;
    private String state;
    private long userId;
}
