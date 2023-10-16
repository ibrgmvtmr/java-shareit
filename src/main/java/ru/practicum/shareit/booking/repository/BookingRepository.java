package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerIdAndStatusAndStartIsBeforeAndEndIsBeforeOrderByIdDesc(long userId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStatusAndStartIsBeforeAndEndIsBeforeOrderByIdDesc(long userId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end);

    Booking findFirstByItemOwnerIdAndStartIsBeforeAndStatusOrderByStartDesc(long userId, LocalDateTime start, BookingStatus bookingStatus);

    Booking findFirstByItemOwnerIdAndStartIsAfterAndStatusOrderByStartAsc(long userId, LocalDateTime start, BookingStatus statusBooking);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(long authorId, long itemId, BookingStatus approved);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdOrderByIdDesc(long userId);

    List<Booking> findAllByBookerIdOrderByIdDesc(long userId);

}
