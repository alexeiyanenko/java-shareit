package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "Фёдор", "fedor@gmail.com");
    }

    @Test
    void shouldCreateUser_WhenRequestIsValid() {
        when(userService.addUser(any())).thenReturn(userDto);

        UserDto response = userController.addUser(userDto);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void shouldDeleteUser_WhenValidRequest() {
        doNothing().when(userService).deleteUser(anyLong());

        assertDoesNotThrow(() -> userController.deleteUser(1L));

        verify(userService, times(1)).deleteUser(1L);
    }
}