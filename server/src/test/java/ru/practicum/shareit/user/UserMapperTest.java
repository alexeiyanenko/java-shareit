package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapperTest {

    @Test
    void shouldMapUserToUserDto() {
        User user = new User(1L, "Фёдор", "fedor@gmail.com");

        UserDto userDto = UserMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void shouldMapUserDtoToUser() {
        UserDto userDto = new UserDto(1L, "Фёдор", "fedor@gmail.com");

        User user = UserMapper.fromUserDto(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getName(), user.getName());
        assertEquals(userDto.getEmail(), user.getEmail());
    }
}

