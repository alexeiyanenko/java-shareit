package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
    private List<Item> items;
}
