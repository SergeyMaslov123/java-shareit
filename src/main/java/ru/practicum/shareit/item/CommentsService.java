package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public interface CommentsService {
    CommentDto addComments(CommentDto commentDto, Long itemId, Long userId);
}
