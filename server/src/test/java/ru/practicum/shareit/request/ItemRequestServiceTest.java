package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private User user;
    private NewItemRequest newItemRequest;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Фёдор", "fedor@gmail.com");
        newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("Описание");
        itemRequest = new ItemRequest(1L, newItemRequest.getDescription(), user, LocalDateTime.now());
    }

    @Test
    void shouldCreateRequest_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = service.addRequest(user.getId(), newItemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(newItemRequest.getDescription(), result.getDescription());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.addRequest(user.getId(), newItemRequest));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldReturnRequestsList_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.findAllByRequesterId(user.getId(), Sort.by(Sort.Direction.ASC, "created")))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDto> result = service.get(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExistOnGetRequests() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.get(user.getId()));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldReturnRequestById_WhenRequestExists() {
        when(repository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(itemRequest.getId())).thenReturn(Collections.emptyList());

        ItemRequestDto result = service.findById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
    }

    @Test
    void shouldReturnAllRequests_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(repository.findByRequesterIdNot(user.getId(), Sort.by(Sort.Direction.ASC, "created")))
                .thenReturn(Collections.singletonList(itemRequest));

        List<ItemRequestDto> result = service.getAll(user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequest.getId(), result.get(0).getId());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExistOnGetAllRequests() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getAll(user.getId()));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }
}