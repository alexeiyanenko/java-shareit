package ru.practicum.shareit.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.item.Item;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByItemId(Long itemId);

    List<Comment> findByItemIn(List<Item> items);
}
