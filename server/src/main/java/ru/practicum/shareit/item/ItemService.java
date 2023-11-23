package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    void deleteItem(long userId, long itemId);

    List<ItemDtoBooking> getItemByUserId(long userId, Integer from, Integer size);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDtoBooking getItem(long itemId, long userId);

    List<ItemDto> searchItem(String text, Integer from, Integer size);
}
