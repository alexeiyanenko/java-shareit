package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody NewItemRequest request) {
        return service.addRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.get(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return service.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable long requestId) {
        return service.findById(requestId);
    }
}
