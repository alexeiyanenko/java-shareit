package ru.practicum.shareit.item.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private boolean available;
    @Column(name = "last_booking")
    private LocalDateTime lastBooking = LocalDateTime.now();
    @Column(name = "next_booking")
    private LocalDateTime nextBooking = LocalDateTime.now();
    @JoinColumn(name = "owner_id")
    @ManyToOne
    private User owner;
    @JoinColumn(name = "request_id")
    @ManyToOne
    private ItemRequest request;

}
