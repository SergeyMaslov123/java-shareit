package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;

import java.time.Instant;

@Value
@Generated
public class ItemRequestDtoForAll {
    long id;
    String description;
    Instant created;
}
