package ru.practicum.shareit.booking;

public interface BookingService {
    Booking get(int id);

    Booking create(Booking booking);

    Booking update(Booking booking);

    void delete(int id);
}
