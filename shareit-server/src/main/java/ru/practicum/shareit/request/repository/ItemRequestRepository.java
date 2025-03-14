package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Sort;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
   List<ItemRequest> findAllByRequesterId(long requesterId, Sort sort);

   List<ItemRequest> findByRequesterIdNot(long requesterId, Sort sort);
}
