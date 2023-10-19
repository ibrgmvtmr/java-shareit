package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    @Id
    @Column(name = "booking_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", nullable = false)
    private Item item;

    @JoinColumn(name = "booker_id", referencedColumnName = "user_id", nullable = false)
    private long bookerId;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private BookingStatus status;

}