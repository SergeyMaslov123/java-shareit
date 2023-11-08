package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentsService commentsService;

    @GetMapping
    public List<ItemDtoBooking> getItemByUser(@RequestHeader(HeaderName.header) Long userId) {
        return itemService.getItemByUserId(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(HeaderName.header) Long userId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader(HeaderName.header) Long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking getItemById(@RequestHeader(HeaderName.header) Long userId,
                                      @PathVariable Long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HeaderName.header) Long userId,
                              @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HeaderName.header) Long userId,
                                 @PathVariable Long itemId, @RequestBody CommentDto commentsDto) {
        return commentsService.addComments(commentsDto, itemId, userId);
    }

}
