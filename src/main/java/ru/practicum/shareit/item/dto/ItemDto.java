package ru.practicum.shareit.item.dto;

import lombok.Value;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Validated
public class ItemDto {
    Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255)
    String name;
    @Size(max = 255)
    @NotBlank(groups = Marker.OnCreate.class)
    String description;
    @NotNull(groups = Marker.OnCreate.class)
    Boolean available;
    Long request;
}
