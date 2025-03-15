package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Фёдор", "fedor@gmail.com");
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExistOnDelete() {
        long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(nonExistentUserId));

        assertEquals("Пользователь с id = " + nonExistentUserId + " не найден", exception.getMessage());
    }

    @Test
    void shouldThrowInternalServerException_WhenEmailIsEmptyOnAddUser() {
        UserDto userDtoWithoutEmail = new UserDto(1L, "Фёдор", "");

        InternalServerException exception = assertThrows(InternalServerException.class,
                () -> userService.addUser(userDtoWithoutEmail));

        assertEquals("Ошибка: email не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExistOnUpdate() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(user.getId(), new UserDto(user.getId(), "Updated Name", "updated@gmail.com")));

        assertEquals("Пользователь с id = " + user.getId() + " не найден", exception.getMessage());
    }

    @Test
    void shouldThrowInternalServerException_WhenEmailAlreadyExists() {
        UserDto newUser = new UserDto(2L, "Другой пользователь", user.getEmail());
        when(userRepository.findAll()).thenReturn(List.of(user));

        InternalServerException exception = assertThrows(InternalServerException.class,
                () -> userService.addUser(newUser));

        assertEquals("Ошибка: Email " + user.getEmail() + " уже используется", exception.getMessage());
    }

    @Test
    void shouldGetUserById_WhenUserExists() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void shouldThrowNotFoundException_WhenUserDoesNotExistOnGetById() {
        long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserById(nonExistentUserId));

        assertEquals("Пользователь с id = " + nonExistentUserId + " не найден", exception.getMessage());
    }
}