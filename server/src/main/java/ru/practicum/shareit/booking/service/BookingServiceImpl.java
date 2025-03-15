package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(Long userId, Long itemId, LocalDateTime start, LocalDateTime end) {
        log.info("Добавление бронирования: userId = {}, itemId = {}", userId, itemId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id = " + itemId + " не найден"));

        if (!item.isAvailable()) {
            throw new ValidationException("Предмет с id = " + item.getId() + " недоступен для бронирования");
        }

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(Booking.StatusType.WAITING);

        log.info("Бронирование создано: {}", booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBookingRequest(long userId, long bookingId, boolean approved) {
        log.info("Обновление бронирования: bookingId = {}, userId = {}, approved = {}", bookingId, userId, approved);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));

        if (userId != booking.getItem().getOwner().getId()) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем предмета");
        }

        booking.setStatus(approved ? Booking.StatusType.APPROVED : Booking.StatusType.REJECTED);
        log.info("Статус бронирования обновлен: {}", booking.getStatus());

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        log.info("Запрос бронирования: bookingId = {}, userId = {}", bookingId, userId);

        Booking booking = bookingRepository.findByBookerIdAndId(userId, bookingId);
        if (booking == null) {
            throw new NotFoundException("Бронирование с id = " + bookingId + " для пользователя с id = " + userId + " не найдено");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingByUser(long userId, String state) {
        log.info("Получение бронирований пользователя: userId = {}, state = {}", userId, state);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentBookings(userId);
            case "PAST" -> bookingRepository.findPastBookings(userId);
            case "FUTURE" -> bookingRepository.findFutureBookings(userId);
            case "WAITING" -> bookingRepository.findBookingByBookerIdAndStatus(userId, "WAITING");
            case "REJECTED" -> bookingRepository.findBookingByBookerIdAndStatus(userId, "REJECTED");
            default -> bookingRepository.findByBookerId(userId);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsByOwner(long userId, String state) {
        log.info("Получение бронирований владельца: ownerId = {}, state = {}", userId, state);

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Sort sort = Sort.by(Sort.Direction.ASC, "start");

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentBookingsByOwner(userId);
            case "PAST" -> bookingRepository.findPastBookingsByOwner(userId);
            case "FUTURE" -> bookingRepository.findFutureBookingsByOwner(userId);
            case "WAITING" -> bookingRepository.findBookingByBookerIdAndStatus(userId, "WAITING");
            case "REJECTED" -> bookingRepository.findBookingByBookerIdAndStatus(userId, "REJECTED");
            default -> bookingRepository.findByItemId(userId, sort);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}