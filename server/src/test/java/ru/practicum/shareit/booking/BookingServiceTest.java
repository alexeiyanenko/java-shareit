package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Пётр", "petr@gmail.com");
        item = new Item(1L, "Вещь", "Описание", true, null, null, user, null);
        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Booking.StatusType.WAITING);
    }

    @Test
    void shouldCreateBooking_WhenUserAndItemExistAndItemIsAvailable() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(user.getId(), item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(user.getId(), item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundException_WhenItemDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(user.getId(), item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertEquals("Предмет с id = " + item.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldThrowValidationException_WhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(user.getId(), item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));

        assertEquals("Предмет с id = " + item.getId() + " недоступен для бронирования", exception.getMessage());
    }

    @Test
    void shouldReturnBookingDto_WhenBookingExists() {
        when(bookingRepository.findByBookerIdAndId(user.getId(), booking.getId())).thenReturn(booking);

        BookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenBookingDoesNotExist() {
        when(bookingRepository.findByBookerIdAndId(user.getId(), booking.getId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(user.getId(), booking.getId()));

        assertEquals("Бронирование с id = " + booking.getId() +
                " для пользователя с id = " + user.getId() + " не найдено", exception.getMessage());
    }

    @Test
    void shouldReturnListOfBookings_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(user.getId())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getBookingByUser(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExistWhileFetchingBookings() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingByUser(user.getId(), "ALL"));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldReturnBookingsByOwner_WhenOwnerExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingsByOwner(user.getId())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getBookingsByOwner(user.getId(), "CURRENT");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenOwnerDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsByOwner(user.getId(), "CURRENT"));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }
}