package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.NewBookingRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    private NewBookingRequest newBookingRequest;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Пётр", "fjk@mail.ru");
        item = new Item(1L, "Вещь", "Описание", true, null, null,
                user, null);
        newBookingRequest = new NewBookingRequest(item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        booking = BookingMapper.fromBookingRequest(newBookingRequest, item, user);
        booking.setStatus(Booking.StatusType.WAITING);
    }

    @Test
    void addBooking_ShouldReturnBookingDto_WhenUserAndItemExistAndItemIsAvailable() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(user.getId(), newBookingRequest);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void addBooking_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), newBookingRequest);
        });

        assertEquals("Пользователь с id = 1 не найден", exception.getMessage());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user.getId(), newBookingRequest);
        });

        assertEquals("Предмет с id = 1 не найден", exception.getMessage());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_ShouldThrowValidationException_WhenItemIsNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.addBooking(user.getId(), newBookingRequest);
        });

        assertEquals("Предмет с id = 1 недоступен для бронирования", exception.getMessage());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).findById(item.getId());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookingById_ShouldReturnBookingDto_WhenBookingExists() {
        when(bookingRepository.findByBookerIdAndId(user.getId(), booking.getId())).thenReturn(booking);

        BookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        verify(bookingRepository).findByBookerIdAndId(user.getId(), booking.getId());
    }

    @Test
    void getBookingById_ShouldThrowNotFoundException_WhenBookingDoesNotExist() {
        when(bookingRepository.findByBookerIdAndId(user.getId(), booking.getId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingById(user.getId(), booking.getId());
        });

        assertEquals("Бронирование с id = " + booking.getId() + " для пользователя с id = " + user.getId() + " не найдено", exception.getMessage());
        verify(bookingRepository).findByBookerIdAndId(user.getId(), booking.getId());
    }

    @Test
    void getBookingByUser_ShouldReturnListOfBookingDto_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerId(user.getId())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getBookingByUser(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        verify(userRepository).findById(user.getId());
        verify(bookingRepository).findByBookerId(user.getId());
    }

    @Test
    void getBookingByUser_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingByUser(user.getId(), "ALL");
        });

        assertEquals("Пользователь с id = 1 не найден", exception.getMessage());
        verify(bookingRepository, never()).findByBookerId(anyLong());
    }

    @Test
    void getBookingsByOwner_ShouldReturnListOfBookingDto_WhenOwnerExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findCurrentBookingsByOwner(user.getId())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getBookingsByOwner(user.getId(), "CURRENT");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        verify(userRepository).findById(user.getId());
        verify(bookingRepository).findCurrentBookingsByOwner(user.getId());
    }

    @Test
    void getBookingsByOwner_ShouldThrowNotFoundException_WhenOwnerDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getBookingsByOwner(user.getId(), "CURRENT");
        });

        assertEquals("Пользователь с id = 1 не найден", exception.getMessage());
        verify(bookingRepository, never()).findCurrentBookingsByOwner(anyLong());
    }
}
