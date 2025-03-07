package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import ru.practicum.shareit.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i " +
            "from Item as i " +
            "where (i.name ilike concat('%', ?1, '%') or i.description ilike concat('%', ?1, '%')) " +
            "and i.available = true")
    List<Item> searchByText(String text);

    List<Item> findAllByOwnerId(Long userId);
}
