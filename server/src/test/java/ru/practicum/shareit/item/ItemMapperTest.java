package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemMapperTest {

    private User owner;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Фёдор", "fedor@gmail.com");
        itemRequest = new ItemRequest(1L, "Нужен ноутбук", owner, LocalDateTime.now());
        item = new Item(1L, "Ноутбук", "Игровой ноутбук", true, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), owner, itemRequest);
    }

    @Test
    void shouldMapItemToItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
    }

    @Test
    void shouldMapItemToItemDtoWithComments() {
        List<Comment> comments = Collections.emptyList();
        ItemDto itemDto = ItemMapper.toItemDtoWithComments(item, comments);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
        assertEquals(0, itemDto.getComments().size());
    }

    @Test
    void shouldMapItemToItemDtoWithRequest() {
        ItemDto itemDto = ItemMapper.toItemDtoWithRequest(item);

        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());
        assertEquals(item.getOwner().getId(), itemDto.getOwnerId());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void shouldMapItemDtoToItem() {
        ItemDto itemDto = new ItemDto(1L, "Ноутбук", "Игровой ноутбук", true,
                null, null, 1L, Collections.emptyList(), null);

        Item mappedItem = ItemMapper.fromItemDto(itemDto);

        assertNotNull(mappedItem);
        assertEquals(itemDto.getId(), mappedItem.getId());
        assertEquals(itemDto.getName(), mappedItem.getName());
        assertEquals(itemDto.getDescription(), mappedItem.getDescription());
        assertEquals(itemDto.getAvailable(), mappedItem.isAvailable());
    }
}
