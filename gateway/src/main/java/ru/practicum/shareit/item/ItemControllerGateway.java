package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ConstatntsGateway;
import ru.practicum.shareit.MarkerGeteway;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemControllerGateway {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemByUser(@RequestHeader(ConstatntsGateway.HEADER) Long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.getItemByUser(userId, from, size);
    }

    @PostMapping
    @Validated({MarkerGeteway.OnCreate.class})
    public ResponseEntity<Object> addItem(@RequestHeader(ConstatntsGateway.HEADER) Long userId, @Valid @RequestBody ItemRequestDto itemDto) {
        return itemClient.addItem(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(ConstatntsGateway.HEADER) Long userId, @PathVariable Long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(ConstatntsGateway.HEADER) Long userId, @PathVariable Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    @Validated(MarkerGeteway.OnUpdate.class)
    public ResponseEntity<Object> updateItem(@RequestHeader(ConstatntsGateway.HEADER) Long userId, @Valid @RequestBody ItemRequestDto itemDto, @PathVariable Long itemId) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComments(@RequestHeader(ConstatntsGateway.HEADER) Long userId, @PathVariable Long itemId, @Valid @RequestBody CommentRequestDto commentDto) {
        return itemClient.addComments(commentDto, itemId, userId);
    }


}
