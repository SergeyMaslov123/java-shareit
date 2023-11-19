package ru.practicum.shareit.item.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class CommentDtoRequest {
    long id;
    @Size(max = 255)
    @NotBlank
    String text;
}
