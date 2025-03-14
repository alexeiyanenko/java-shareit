package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.error("Ошибка при добавлении пользователя: отсутствует email");
            throw new InternalServerException("Ошибка: email не может быть пустым");
        }

        checkEmailDuplicate(userDto);
        User user = UserMapper.fromUserDto(userDto);
        User savedUser = userRepository.save(user);

        log.info("Пользователь с id = {} успешно добавлен", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        log.info("Начато обновление пользователя с id = {}", userId);

        checkEmailDuplicate(userDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        User updatedUser = userRepository.save(user);
        log.info("Пользователь с id = {} успешно обновлен", updatedUser.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(long userId) {
        log.info("Запрос информации о пользователе с id = {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        log.info("Информация о пользователе с id = {} успешно получена", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(long userId) {
        log.info("Попытка удаления пользователя с id = {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        userRepository.delete(user);
        log.info("Пользователь с id = {} успешно удален", userId);
    }

    private void checkEmailDuplicate(UserDto userDto) {
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(userDto.getEmail()));

        if (emailExists) {
            log.error("Ошибка: Email {} уже используется", userDto.getEmail());
            throw new InternalServerException("Ошибка: Email " + userDto.getEmail() + " уже используется");
        }
    }
}