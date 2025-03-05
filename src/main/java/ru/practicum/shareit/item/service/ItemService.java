package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto newItemDto);

    ItemWithBookingDto getItemById(Long itemId, Long userId);

    List<ItemWithBookingDto> getItemsByUserId(Long id);

    List<ItemDto> getItemsByText(String text);
}
