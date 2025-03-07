package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

import ru.practicum.shareit.user.dto.UserDto;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
}
