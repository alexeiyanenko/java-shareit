package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated(),
                new ArrayList<>()
        );
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(itemRequestDto.getRequester());
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public static ItemRequest fromNewItemRequest(NewItemRequest request, User requester, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(created);
        return itemRequest;
    }
}
