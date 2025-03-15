package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import ru.practicum.shareit.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(long userId);

    @Query("SELECT i FROM Item i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))")
    List<Item> findAllByText(String text);

    List<Item> findByRequestId(long id);

}
