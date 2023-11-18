package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;

import javax.validation.constraints.NotBlank;

@Value
@Generated
public class ItemRequestDto {
    long id;
    @NotBlank
    String description;
}
