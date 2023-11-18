package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;

@Value
@Generated
public class CommentDto {
    Long id;
    String text;
    Long idItem;
    String authorName;
    String created;
}
