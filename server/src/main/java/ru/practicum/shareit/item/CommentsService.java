package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;


public interface CommentsService {
    CommentDto addComments(CommentDtoRequest commentDtoRequest, Long itemId, Long userId);
}
