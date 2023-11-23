package ru.practicum.shareit.request.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class ItemRequestDtoForAll {
    long id;
    String description;
    Instant created;
}
