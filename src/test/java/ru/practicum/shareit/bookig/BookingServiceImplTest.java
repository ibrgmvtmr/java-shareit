package ru.practicum.shareit.bookig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingWithStateDto;
import ru.practicum.shareit.booking.mappers.BookingMappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.BookingTimeException;
import ru.practicum.shareit.exceptions.ItemUnavailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    public void testGetBooking() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(2);
        User user = User.builder().id(1L).name("Name").email("email").build();
        User user2 = User.builder().id(3L).name("Name").email("email").build();
        Item item = Item.builder().name("item01").owner(user).id(1L).description("description").available(true).requestId(1L).build();
        Booking booking = Booking.builder().id(1L).status(BookingStatus.APPROVED).end(startDate).start(endDate).item(item).bookerId(1).build();

        BookingWithStateDto bookingWithStateDto = BookingMappers.toBookingWithStateDto(1, "true", 1);
        Assertions.assertNotNull(bookingWithStateDto);
        BookingDto bookingDto = BookingMappers.toBookingDto(booking);
        Assertions.assertNotNull(bookingDto);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        User userOne = User.builder().id(1L).build();

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userOne));

        BookingWithStateDto bookingWithStateDto2 = BookingWithStateDto.builder().id(1).userId(1).state("CURRENT").build();
        Assertions.assertEquals(bookingDto.getId(), bookingService.getBooking(bookingWithStateDto2).getId());


        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookingWithStateDto2));


        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookingWithStateDto2));

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(userOne));
        booking.setBookerId(99L);
        booking.getItem().setOwner(user2);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBooking(bookingWithStateDto2));

        booking.getItem().setOwner(user);
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        Assertions.assertNotNull(bookingService.getBooking(bookingWithStateDto2));
    }

    @Test
    public void testCreateBooking() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = startDate.plusDays(2);
        User user = User.builder().id(1L).name("Name").email("email").build();
        User user2 = User.builder().id(2L).name("Name").email("email").build();
        Item item = Item.builder().name("item01").owner(user).id(1L).description("description").available(true).requestId(1L).build();
        Booking booking = Booking.builder().id(1L).status(BookingStatus.APPROVED).end(startDate).start(endDate).item(item).bookerId(2).build();
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);
        BookingShortDto bookingShortDto = BookingShortDto.builder().id(1).itemId(1L).end(endDate).start(startDate).bookerId(1L).status(BookingStatus.APPROVED).build();
        BookingDto bookingDto = BookingDto.builder().id(1L).status(BookingStatus.APPROVED).start(startDate).end(endDate).item(item).booker(user).build();

        long userId = 2L;
        BookingDto bookingDtoTest = bookingService.create(bookingShortDto, userId);

        Assertions.assertEquals(bookingDto.getId(), bookingDtoTest.getId());

        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.create(bookingShortDto, userId));

        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        item.setAvailable(false);
        Assertions.assertThrows(ItemUnavailableException.class, () -> bookingService.create(bookingShortDto, userId));

        item.setOwner(user2);

        bookingShortDto.setEnd(startDate);
        Assertions.assertThrows(BookingTimeException.class, () -> bookingService.create(bookingShortDto, userId));
        bookingShortDto.setStart(LocalDateTime.now().minusHours(2));
        Assertions.assertThrows(BookingTimeException.class, () -> bookingService.create(bookingShortDto, userId));
        bookingShortDto.setStart(endDate);
        Assertions.assertThrows(BookingTimeException.class, () -> bookingService.create(bookingShortDto, userId));


    }

    @Test
    public void testUpdateBookingStatusById() {
        User user = User.builder().id(1L).name("Name").email("email").build();
        User user3 = User.builder().id(3L).name("Name").email("email").build();
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(2);
        Item item = Item.builder().name("item01").owner(user).id(1L).description("description").available(true).requestId(1L).build();
        Booking preSaveBooking = Booking.builder().id(1L).status(BookingStatus.WAITING).end(startDate).start(endDate).item(item).bookerId(1).build();
        Booking afterSaveBooking = Booking.builder().id(1L).status(BookingStatus.APPROVED).end(startDate).start(endDate).item(item).bookerId(1).build();
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(preSaveBooking));
        when(bookingRepository.save(Mockito.any())).thenReturn(afterSaveBooking);
        BookingWithStateDto bookingInputStatusDto = BookingWithStateDto.builder().id(1).state("true").userId(1).build();

        preSaveBooking.getItem().setOwner(user3);
        preSaveBooking.setStatus(BookingStatus.WAITING);
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(bookingInputStatusDto));
        bookingInputStatusDto.setState("false");
        preSaveBooking.getItem().setOwner(user);
        Assertions.assertEquals(BookingStatus.REJECTED, bookingService.updateBookingStatus(bookingInputStatusDto).getStatus());
        preSaveBooking.getItem().setOwner(user3);
        Assertions.assertEquals(BookingStatus.CANCELED, bookingService.updateBookingStatus(bookingInputStatusDto).getStatus());


        preSaveBooking.getItem().setOwner(user3);
        preSaveBooking.setBookerId(99);
        preSaveBooking.setStatus(BookingStatus.WAITING);
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(bookingInputStatusDto));

        preSaveBooking.setStatus(BookingStatus.APPROVED);
        Assertions.assertThrows(ItemUnavailableException.class, () -> bookingService.updateBookingStatus(bookingInputStatusDto).toString());


        bookingInputStatusDto.setState("sdf");
        Assertions.assertThrows(ItemUnavailableException.class, () -> bookingService.updateBookingStatus(bookingInputStatusDto));

        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(bookingInputStatusDto));

    }

}
