package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Validated
public class CommentDto {
    Long id;
    @Size(max = 255)
    @NotBlank
    String text;
    Long idItem;
    String authorName;
    String created;
}
