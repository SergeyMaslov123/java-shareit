package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.request.ItemRequest;

@Value
public class ItemDto {
    String name;
    String description;
    Boolean available;
    Integer owner;
    int id;
    ItemRequest request;
}
