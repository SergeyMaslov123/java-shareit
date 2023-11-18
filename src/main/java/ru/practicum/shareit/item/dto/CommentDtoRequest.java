package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Generated
public class CommentDtoRequest {
    long id;
    @Size(max = 255)
    @NotBlank
    String text;
}
