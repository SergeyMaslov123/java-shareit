package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        userRepository.getUser(userId);
        Item item = itemMapper.toDtoItem(itemDto);
        item.setOwner(userId);
        return itemMapper.toItemDto(itemRepository.addItem(item));
    }

    @Override
    public void deleteItem(int userId, int itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    @Override
    public List<ItemDto> getItemByUserId(int userId) {
        return itemRepository.getItemsByUser(userId).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(int userId, int itemId, ItemDto itemDto) {
        Item item = itemMapper.toDtoItem(itemDto);
        return itemMapper.toItemDto(itemRepository.updateItem(userId, itemId, item));
    }

    @Override
    public ItemDto getItem(int itemId) {
        return itemMapper.toItemDto(itemRepository.getItem(itemId));

    }

    @Override
    public List<ItemDto> searchItem(String text) {
        return itemRepository.searchItem(text).stream()
                .map(item -> itemMapper.toItemDto(item))
                .collect(Collectors.toList());
    }
}
