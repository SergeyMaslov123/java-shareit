package ru.practicum.shareit.user.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Value
@Generated
public class UserDto {
    Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String name;
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    @NotEmpty(groups = Marker.OnCreate.class)
    @Size(max = 512, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    String email;
}
