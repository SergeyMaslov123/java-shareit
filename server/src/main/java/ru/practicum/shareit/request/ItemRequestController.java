package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.Constants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswerThenCreate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoAnswerThenCreate addRequest(@RequestHeader(Constants.HEADER) Long userId,
                                                     @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoAnswer> getAllItemRequestForUser(@RequestHeader(Constants.HEADER) Long userId) {
        return itemRequestService.getItemRequestForUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoAnswer> getAllItemRequest(@RequestHeader(Constants.HEADER) Long userId,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoAnswer getItemRequestById(@RequestHeader(Constants.HEADER) Long userId,
                                                   @PathVariable Long requestId) {
        return itemRequestService.getItemRequestForId(userId, requestId);
    }

}
