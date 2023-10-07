package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private int id;
    private Item item;
    private Instant start;
    private Instant end;
    private StatusBooking status;
    private User broker;
}
