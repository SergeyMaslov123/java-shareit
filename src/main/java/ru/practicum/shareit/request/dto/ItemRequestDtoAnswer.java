package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.Instant;
import java.util.List;

@Value
@Generated
public class ItemRequestDtoAnswer {
    long id;
    String description;
    Instant created;
    User requestor;
    List<ItemDto> items;
}
