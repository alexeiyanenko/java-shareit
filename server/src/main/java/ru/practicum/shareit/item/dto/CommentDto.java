package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import ru.practicum.shareit.item.model.Item;

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private Item item;
    private String authorName;
    private LocalDateTime created;
}
