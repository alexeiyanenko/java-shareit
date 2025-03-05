package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с id " + userId + " не найден"));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto) {
        checkUser(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Вещь с id " + itemId + " не найдена"));
        if (!item.getOwner().getId().equals(userId)) {
            log.warn("Доступ запрещен: пользователь с id {} не владеет вещью с id {}", userId, itemId);
            throw new AccessDeniedException("Доступ запрещен: пользователь c id " + userId + " не владеет вещью с id " + itemId);
        }
        if (newItemDto.getName() != null) {
            item.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            item.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            item.setAvailable(newItemDto.getAvailable());
        }
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemWithBookingDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException(
                "Вещь с id " + itemId + " не найдена"));
        List<Booking> bookings = bookingRepository.findByItemAndStatusOrderByStart(item, BookingStatus.APPROVED);
        List<BookingDtoForItem> bookingDtoList = bookings.stream()
                .map(BookingMapper::toBookingDtoForItem)
                .collect(toList());

        ItemWithBookingDto itemWithBookingDto = ItemMapper.toItemWithBookingDto(item);

        if (item.getOwner().getId().equals(userId)) {
            BookingDtoForItem lastBooking = getLastBooking(bookingDtoList);
            BookingDtoForItem nextBooking = getNextBooking(bookingDtoList);
            itemWithBookingDto.setLastBooking(lastBooking);
            itemWithBookingDto.setNextBooking(nextBooking);
        }
        itemWithBookingDto.setComments(commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .toList());

        return itemWithBookingDto;
    }

    @Override
    public List<ItemWithBookingDto> getItemsByUserId(Long id) {
        checkUser(id);
        List<Item> items = itemRepository.findAllByOwnerId(id);

        Map<Long, List<BookingDtoForItem>> bookings = bookingRepository.findAllByItemInAndStatusOrderByStartAsc(
                        items, BookingStatus.APPROVED).stream()
                .map(BookingMapper::toBookingDtoForItem)
                .collect(groupingBy(BookingDtoForItem::getItemId, toList()));

        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIn(items).stream()
                .map(CommentMapper::toCommentDto)
                .collect(groupingBy(CommentDto::getItemId, toList()));

        List<ItemWithBookingDto> itemWithBookingDtoList = items.stream()
                .map(ItemMapper::toItemWithBookingDto)
                .toList();

        for (ItemWithBookingDto item : itemWithBookingDtoList) {
            item.setLastBooking(getLastBooking(bookings.get(item.getId())));
            item.setNextBooking(getNextBooking(bookings.get(item.getId())));
            item.setComments(comments.getOrDefault(item.getId(), Collections.emptyList()));
        }
        return itemWithBookingDtoList;
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                "Пользователь с id " + userId + " не найден"));
    }

    private BookingDtoForItem getLastBooking(List<BookingDtoForItem> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(BookingDtoForItem -> BookingDtoForItem.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingDtoForItem::getStart))
                .orElse(null);
    }

    private BookingDtoForItem getNextBooking(List<BookingDtoForItem> bookings) {
        if (bookings == null || bookings.isEmpty()) {
            return null;
        }
        return bookings.stream()
                .filter(BookingDtoForItem -> BookingDtoForItem.getStart().isAfter(LocalDateTime.now()))
                .findFirst()
                .orElse(null);
    }
}
