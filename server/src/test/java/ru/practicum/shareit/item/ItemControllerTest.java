package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Вещь", "Описание", true,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                1L, Collections.emptyList(), null);
    }

    @Test
    void shouldCreateItem_WhenRequestIsValid() {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemDto);

        ItemDto response = itemController.addItem(1L, itemDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldReturnItemById_WhenItemExists() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        ItemDto response = itemController.getItemById(1L, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldUpdateItem_WhenValidRequest() {
        when(itemService.updateItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        ItemDto response = itemController.updateItem(1L, 1L, itemDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }
}