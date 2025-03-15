package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (itemDto.getName() == null || itemDto.getName().isEmpty() ||
                itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            log.error("Ошибка при добавлении вещи: не указано имя или описание");
            throw new InternalServerException("Ошибка: имя или описание вещи не может быть пустым");
        }

        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(user);
        item.setLastBooking(null);
        item.setNextBooking(null);

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос на вещь с id = " + itemDto.getRequestId() + " не найден"));

            item.setRequest(itemRequest);
            log.info("Вещь с id = {} добавлена пользователем с id = {}", item.getId(), userId);
            return ItemMapper.toItemDtoWithRequest(itemRepository.save(item));
        }

        log.info("Вещь с id = {} добавлена пользователем с id = {}", item.getId(), userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        List<Item> userItems = itemRepository.findByOwnerId(userId);
        Item item = userItems.stream().filter(i -> i.getId() == itemId).findFirst()
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена у пользователя с id = " + userId));

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        log.info("Вещь с id = {} обновлена пользователем с id = {}", item.getId(), userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        Sort sort = Sort.by(Sort.Direction.ASC, "start");

        if (!bookingRepository.findByBookerId(userId).isEmpty()) {
            bookingRepository.findByItemId(itemId, sort).stream()
                    .filter(booking -> booking.getItem().equals(item))
                    .findFirst()
                    .ifPresent(booking -> {
                        if (booking.getStart().isAfter(LocalDateTime.now()) && item.getNextBooking() == null) {
                            item.setNextBooking(booking.getStart());
                        }
                        if (booking.getEnd().isAfter(LocalDateTime.now())) {
                            item.setLastBooking(booking.getEnd());
                        }
                    });
        }

        log.info("Пользователь с id = {} получил информацию о вещи с id = {}", userId, itemId);
        return ItemMapper.toItemDtoWithComments(item, commentRepository.findAll());
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        log.info("Пользователь с id = {} запросил список всех своих вещей", userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            log.info("Поиск по пустой строке, возвращен пустой список");
            return new ArrayList<>();
        }

        log.info("Выполняется поиск вещей по запросу: '{}'", text);
        return itemRepository.findAllByText(text).stream()
                .filter(Item::isAvailable)
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public CommentDto comment(long userId, long itemId, NewCommentRequest request) {
        log.info("Пользователь с id = {} добавляет комментарий к вещи с id = {}", userId, itemId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        Sort sort = Sort.by(Sort.Direction.ASC, "start");
        boolean hasBooking = bookingRepository.findByItemId(itemId, sort).stream()
                .anyMatch(booking -> userId == booking.getBooker().getId() && booking.getEnd().isBefore(LocalDateTime.now()));

        if (!hasBooking) {
            log.error("Пользователь с id = {} не может оставить комментарий, так как не арендовал вещь с id = {}", userId, itemId);
            throw new ValidationException("Пользователь с id = " + userId + " не может оставить комментарий к вещи с id = " + itemId);
        }

        Comment comment = CommentMapper.fromCommentRequest(request, author, item);
        commentRepository.save(comment);

        log.info("Комментарий пользователя с id = {} добавлен к вещи с id = {}", userId, itemId);
        return CommentMapper.toCommentDto(comment);
    }
}