package ru.practicum.shareit.request.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class ItemRequestDto {
    long id;
    @NotBlank
    String description;
}
