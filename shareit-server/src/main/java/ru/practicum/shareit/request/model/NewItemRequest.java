package ru.practicum.shareit.request.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewItemRequest {
    private String description;
}
