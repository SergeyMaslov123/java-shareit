package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int userId);

    void deleteItem(int userId, int itemId);

    List<ItemDto> getItemByUserId(int userId);

    ItemDto updateItem(int userId, int itemId, ItemDto itemDto);

    ItemDto getItem(int itemId);

    List<ItemDto> searchItem(String text);
}
