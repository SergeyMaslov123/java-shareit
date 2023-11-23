package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class CommentDtoRequest {
    long id;
    String text;
}
