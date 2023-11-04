package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    void deleteByUserIdAndItemId(int userId, int itemId);

    List<Item> getItemsByUser(int userId);

    Item getItem(int itemId);

    Item updateItem(int userId, int itemId, Item item);

    List<Item> searchItem(String text);
}
