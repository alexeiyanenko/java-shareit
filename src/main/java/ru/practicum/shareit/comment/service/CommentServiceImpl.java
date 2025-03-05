package ru.practicum.shareit.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = checkUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Вещь с id " + itemId + " не найдена"));

        List<Booking> bookings = bookingRepository.findBookingForComment(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BookingValidationException("Бронь не найдена");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с id " + userId + " не найден"));
    }
}
