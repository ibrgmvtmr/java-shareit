package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    private long id;
    @NonNull
    private Long itemId;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    private BookingStatus status;
    private Long bookerId;
}