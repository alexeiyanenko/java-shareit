package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
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
    void addItem_ShouldReturnItemDto_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.addItem(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        verify(userRepository).findById(user.getId());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void addItem_ShouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));

        assertEquals("Пользователь с id = 1 не найден", exception.getMessage());
        verify(userRepository).findById(user.getId());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void search_ShouldReturnEmptyList_WhenQueryIsEmpty() {
        List<ItemDto> result = itemService.search("");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}