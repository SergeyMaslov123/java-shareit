package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemByUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemByUserId(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") int userId,
                           @RequestBody ItemDto itemDto) {
        return itemService.addItem(itemDto, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") int userId,
                           @PathVariable int itemId) {
        itemService.deleteItem(userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") int userId,
                              @RequestBody ItemDto itemDto, @PathVariable int itemId) {
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
