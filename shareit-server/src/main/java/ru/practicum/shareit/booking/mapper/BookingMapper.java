package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import ru.practicum.shareit.booking.model.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking fromBookingRequest(NewBookingRequest bookingRequest, Item item, User user) {
        Booking booking = new Booking();

        booking.setStart(bookingRequest.getStart());
        booking.setEnd(bookingRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

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
