package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface ItemService {
    ItemDto addItem(@Valid ItemDto itemDto, long userId);

    void deleteItem(long userId, long itemId);

    List<ItemDtoBooking> getItemByUserId(long userId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemDtoBooking getItem(long itemId, long userId);

    List<ItemDto> searchItem(String text);
}
