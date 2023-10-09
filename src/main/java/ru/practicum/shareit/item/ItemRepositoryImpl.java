package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private Map<Integer, List<Item>> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        item.setId(getId());
        validateItem(item);
        items.compute(item.getOwner(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public void deleteByUserIdAndItemId(int userId, int itemId) {
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId() == itemId);
        }
    }

    @Override
    public List<Item> getItemsByUser(int userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item getItem(int itemId) {
        if (items.values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(anyItem -> anyItem.getId() == itemId)) {
            return items.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(item1 -> item1.getId() == itemId)
                    .findAny().orElseThrow(() -> new NotFoundException("not found id"));
        } else {
            throw new NotFoundException("Item not found");//not found ex
        }
    }

    @Override
    public Item updateItem(int userId, int itemId, Item item) {
        if (items.containsKey(userId)) {
            List<Item> itemsByUser = items.get(userId);
            if (itemsByUser.stream().anyMatch(item1 -> item1.getId() == itemId)) {
                itemsByUser.forEach(item1 -> {
                    if (item1.getId() == itemId) {
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
                    }
                });
                items.put(userId, itemsByUser);
                return getItem(itemId);
            } else {
                throw new NotFoundException("Item not found");
            }
        } else {
            throw new NotFoundException("User not item");
        }
    }

    @Override
    public List<Item> searchItem(String text) {
        String textToLower = text.toLowerCase();
        if(text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getName().toLowerCase().equals(textToLower)
                        || item.getDescription().toLowerCase().contains(textToLower))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    private Integer getId() {
        int lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
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
