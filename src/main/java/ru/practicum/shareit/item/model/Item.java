package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentAnswerDto;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 1000, nullable = false)
    private String description;

    @Column(name = "is_available")
    private Boolean available;

    @JoinColumn(name = "owner_id", nullable = false)
    private Long ownerId;

    @OneToOne
    @Transient
    private Booking booking;

    @Transient
    private List<CommentAnswerDto> comments;

    @Transient
    private BookingShortDto lastBooking;

    @Transient
    private BookingShortDto nextBooking;
}
