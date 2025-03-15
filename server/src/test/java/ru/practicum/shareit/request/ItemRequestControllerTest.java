package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private NewItemRequest newItemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("Описание");

        itemRequestDto = new ItemRequestDto(1L, "Описание", null, LocalDateTime.now(), List.of());
    }

    @Test
    void shouldCreateRequest_WhenRequestIsValid() {
        when(itemRequestService.addRequest(anyLong(), any())).thenReturn(itemRequestDto);

        ItemRequestDto response = itemRequestController.addRequest(1L, newItemRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldGetRequestById_WhenValidRequest() {
        when(itemRequestService.findById(anyLong())).thenReturn(itemRequestDto);

        ItemRequestDto response = itemRequestController.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }
}