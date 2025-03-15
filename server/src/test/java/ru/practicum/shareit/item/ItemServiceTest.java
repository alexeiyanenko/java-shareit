package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Фёдор", "fedor@gmail.com");
        item = new Item(1L, "Вещь", "Описание", true, null, null, user, null);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), null, null, user.getId(), null, null);
    }

    @Test
    void shouldCreateItem_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.addItem(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addItem(user.getId(), itemDto));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldReturnEmptyList_WhenSearchQueryIsEmpty() {
        List<ItemDto> result = itemService.search("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserIsNotOwnerOfItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(user.getId(), item.getId(), itemDto));

        assertEquals("Вещь с id = " + item.getId() + " не найдена у пользователя с id = " + user.getId(), exception.getMessage());
    }

    @Test
    void shouldReturnEmptyList_WhenUserHasNoItems() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findByOwnerId(user.getId())).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.getAllItems(user.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyList_WhenSearchQueryHasNoMatches() {
        when(itemRepository.findAllByText("no_match")).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.search("no_match");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowValidationException_WhenUserDidNotRentItem() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemId(anyLong(), any())).thenReturn(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.comment(user.getId(), item.getId(), new NewCommentRequest("Great item!")));

        assertEquals("Пользователь с id = " + user.getId() + " не может оставить комментарий к вещи с id = " + item.getId(), exception.getMessage());
    }
}