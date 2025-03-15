package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private BookingDto bookingDto;
    private Map<String, Object> bookingData;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1).toString(),
                LocalDateTime.now().plusDays(2).toString(), new Item(), new User(), "WAITING");

        bookingData = new HashMap<>();
        bookingData.put("itemId", 1L);
        bookingData.put("start", LocalDateTime.now().plusDays(1).toString());
        bookingData.put("end", LocalDateTime.now().plusDays(2).toString());
    }

    @Test
    void shouldCreateBooking_WhenRequestIsValid() {
        when(bookingService.addBooking(anyLong(), anyLong(), any(), any())).thenReturn(bookingDto);

        BookingDto response = bookingController.addBooking(1L, bookingData);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldReturnBookingById_WhenBookingExists() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        BookingDto response = bookingController.getBookingById(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldUpdateBookingStatus_WhenValidRequest() {
        when(bookingService.updateBookingRequest(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        BookingDto response = bookingController.updateBookingRequest(1L, 1L, true);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenBookingDoesNotExist() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenThrow(new NotFoundException("Бронирование не найдено"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingController.getBookingById(1L, 999L));

        assertEquals("Бронирование не найдено", exception.getMessage());
    }

    @Test
    void shouldReturnEmptyList_WhenUserHasNoBookings() {
        when(bookingService.getBookingByUser(anyLong(), anyString())).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingController.getBookingByUser(1L, "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyList_WhenOwnerHasNoBookings() {
        when(bookingService.getBookingsByOwner(anyLong(), anyString())).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingController.getBookingsByOwner(1L, "ALL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}