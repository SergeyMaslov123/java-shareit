package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Value
public class CommentDto {
    Long id;
    String text;
    Long idItem;
    String authorName;
    String created;
}
