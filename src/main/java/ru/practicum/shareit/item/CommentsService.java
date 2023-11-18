package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;

import javax.validation.Valid;

@Generated
public interface CommentsService {
    CommentDto addComments(@Valid CommentDtoRequest commentDtoRequest, Long itemId, Long userId);
}
