package ru.practicum.shareit.bookig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingServiceImpl bookingService;

    private MockMvc mvc;

    @Test
    void testAddBooking() throws Exception {
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .itemId(1L)
                .start(LocalDateTime.parse("2022-01-01T10:00:00"))
                .end(LocalDateTime.parse("2022-01-01T12:00:00"))
                .build();

        BookingDto bookingDto = BookingDto.builder().id(1L).build();

        when(bookingService.create(any(), anyLong())).thenReturn(bookingDto);

        mvc = standaloneSetup(bookingController).build();

        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content("{\n\"itemId\": 1,\n\"start\": \"2022-01-01T10:00:00\",\n\"end\": \"2022-01-01T12:00:00\"\n}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateBooking() throws Exception {
        BookingDto bookingDto = BookingDto.builder().id(1L).build();

        when(bookingService.updateBookingStatus(any())).thenReturn(bookingDto);

        mvc = standaloneSetup(bookingController).build();

        mvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    void testGetBooking() throws Exception {
        BookingDto bookingDto = BookingDto.builder().id(1L).build();

        when(bookingService.getBooking(any())).thenReturn(bookingDto);

        mvc = standaloneSetup(bookingController).build();

        mvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

    }

    @Test
    void testGetAllBookingForUser() throws Exception {
        BookingDto bookingDto = BookingDto.builder().id(1L).build();

        when(bookingService.getAllUserBookings(any(), anyInt(), anyInt())).thenReturn(Arrays.asList(bookingDto));

        mvc = standaloneSetup(bookingController).build();

        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

    }

    @Test
    void testGetAllBookingForOwnerItem() throws Exception {
        BookingDto bookingDto = BookingDto.builder().id(1L).build();

        when(bookingService.getAllOwnerItemBookings(any(), anyInt(), anyInt())).thenReturn(Arrays.asList(bookingDto));

        mvc = standaloneSetup(bookingController).build();

        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

    }

}
