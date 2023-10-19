package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerIdAndStatusAndStartIsBeforeAndEndIsBefore(long userId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerIdAndStatusAndStartIsBeforeAndEndIsBefore(long userId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end, Sort sort);

    Booking findFirstByItemOwnerIdAndStartIsBeforeAndStatusOrderByStartDesc(long userId, LocalDateTime start, BookingStatus bookingStatus);

    Booking findFirstByItemOwnerIdAndStartIsAfterAndStatusOrderByStartAsc(long userId, LocalDateTime start, BookingStatus statusBooking);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(long authorId, long itemId, BookingStatus approved);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime start, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(long userId, Sort sort);

    List<Booking> findAllByBookerIdOrderByIdDesc(long userId);

}
