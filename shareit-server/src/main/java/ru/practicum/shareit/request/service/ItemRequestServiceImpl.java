package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.model.NewItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.toItemRequestDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(long userId, NewItemRequest request) {
        log.info("Пользователь с id = {} создает новый запрос на вещь", userId);

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.fromNewItemRequest(request, requester, LocalDateTime.now());
        ItemRequestDto savedRequest = toItemRequestDto(repository.save(itemRequest));

        log.info("Запрос на вещь с id = {} успешно создан пользователем с id = {}", savedRequest.getId(), userId);
        return savedRequest;
    }

    @Override
    public List<ItemRequestDto> get(long userId) {
        log.info("Пользователь с id = {} запрашивает список своих запросов на вещи", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<ItemRequestDto> requests = repository.findAllByRequesterId(userId, sort).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();

        log.info("Найдено {} запросов на вещи для пользователя с id = {}", requests.size(), userId);
        return requests;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId) {
        log.info("Пользователь с id = {} запрашивает список всех запросов, кроме своих", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        List<ItemRequestDto> requests = repository.findByRequesterIdNot(userId, sort).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();

        log.info("Найдено {} запросов на вещи, не принадлежащих пользователю с id = {}", requests.size(), userId);
        return requests;
    }

    @Override
    public ItemRequestDto findById(long requestId) {
        log.info("Запрос на получение информации о запросе на вещь с id = {}", requestId);

        ItemRequestDto itemRequestDto = toItemRequestDto(repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь с id = " + requestId + " не найден")));

        itemRequestDto.setItems(itemRepository.findByRequestId(requestId));

        log.info("Информация о запросе на вещь с id = {} успешно получена", requestId);
        return itemRequestDto;
    }
}