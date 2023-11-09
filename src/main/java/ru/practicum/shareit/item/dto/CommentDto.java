package ru.practicum.shareit.item.dto;

import lombok.Value;

@Value
public class CommentDto {
    Long id;
    String text;
    Long idItem;
    String authorName;
    String created;
}
