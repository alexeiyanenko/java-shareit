package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        String start = DateTimeFormatter
                .ISO_LOCAL_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(booking.getStart());
        String end = DateTimeFormatter
                .ISO_LOCAL_DATE_TIME
                .withZone(ZoneOffset.UTC)
                .format(booking.getEnd());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setStatus(booking.getStatus().toString());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(booking.getItem());
        return bookingDto;
    }
}
