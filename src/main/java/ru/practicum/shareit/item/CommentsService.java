package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.validation.Valid;

@Validated
public interface CommentsService {
    CommentDto addComments(@Valid CommentDto commentDto, Long itemId, Long userId);
}
