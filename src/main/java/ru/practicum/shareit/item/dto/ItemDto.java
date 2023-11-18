package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Value
@Generated
public class ItemDto {
    Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String name;
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotBlank(groups = Marker.OnCreate.class)
    String description;
    @NotNull(groups = Marker.OnCreate.class)
    Boolean available;
    Long requestId;
}
