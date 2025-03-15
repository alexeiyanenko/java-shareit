package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, Long itemId, LocalDateTime start, LocalDateTime end);

    BookingDto updateBookingRequest(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getBookingByUser(long userId, String state);

    List<BookingDto> getBookingsByOwner(long userId, String state);
}
