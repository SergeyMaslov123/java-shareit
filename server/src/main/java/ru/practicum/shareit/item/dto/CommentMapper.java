package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;

import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        String created = comment.getCreated().atZone(ZoneOffset.UTC).toString();
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                created);
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                null,
                null,
                null);
    }

    public static Comment toCommentFromRequest(CommentDtoRequest commentDtoRequest) {
        return new Comment(null,
                commentDtoRequest.getText(),
                null,
                null,
                null);
    }
}