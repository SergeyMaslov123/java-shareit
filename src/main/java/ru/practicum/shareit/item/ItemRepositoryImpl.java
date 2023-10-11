package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(getId());
        validateItem(item);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int itemId) {
        if (items.containsKey(itemId)) {
            Item item = items.get(itemId);
            if (item.getOwner() == userId) {
                items.remove(itemId);
            } else {
                throw new ValidationEx("Item не принадлежит данному User");
            }
        }
    }

    @Override
    public List<Item> getItemsByUser(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItem(int itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException("Item not found");//not found ex
        }
    }

    @Override
    public Item updateItem(int userId, int itemId, Item item) {
        if (items.containsKey(itemId)) {
            Item item1 = items.get(itemId);
            if (item.getName() != null) {
                validateName(item);
                item1.setName(item.getName());
            }
            if (item.getDescription() != null) {
                validateDescription(item);
                item1.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                item1.setAvailable(item.getAvailable());
            }
            items.put(itemId, item1);
            return item1;
        } else {
            throw new NotFoundException("User not item");
        }
    }

    @Override
    public List<Item> searchItem(String text) {
        String textToLower = text.toLowerCase();
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        } else {
            return items.values().stream()
                    .filter(item -> item.getName().toLowerCase().equals(textToLower)
                            || item.getDescription().toLowerCase().contains(textToLower))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }

    private Integer getId() {
        int lastId = items.values()
                .stream()
                .mapToInt(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    private void validateItem(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank() || item.getDescription().isEmpty() ||
                item.getName() == null || item.getName().isEmpty() || item.getName().isBlank()
                || item.getAvailable() == null) {
            throw new ValidationEx("недопустимые параметры");
        }
    }

    private void validateName(Item item) {
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            throw new ValidationEx("недопустимые параметры name");
        }
    }

    private void validateDescription(Item item) {
        if (item.getDescription().isBlank() || item.getDescription().isEmpty()) {
            throw new ValidationEx("недопустимые параметры Description");
        }
    }
}
