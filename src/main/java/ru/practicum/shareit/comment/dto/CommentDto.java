package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
