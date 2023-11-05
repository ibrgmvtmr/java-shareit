package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerIdAndStatusAndStartIsBeforeAndEndIsBefore(long userId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusAndStartIsBeforeAndEndIsBefore(long userId, BookingStatus bookingStatus, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Booking findFirstByItemOwnerIdAndStartIsBeforeAndStatus(long userId, LocalDateTime start, BookingStatus bookingStatus, Sort sort);

    Booking findFirstByItemOwnerIdAndStartIsAfterAndStatus(long userId, LocalDateTime start, BookingStatus statusBooking, Sort sort);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusOrderByEndAsc(long authorId, long itemId, BookingStatus approved);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsAfter(long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(long userId, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatus(long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerId(long userId, Pageable pageable);

    Page<Booking> findAllByBookerId(long userId, Pageable pageable);

}
