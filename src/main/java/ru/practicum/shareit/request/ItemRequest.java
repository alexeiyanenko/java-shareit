package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
