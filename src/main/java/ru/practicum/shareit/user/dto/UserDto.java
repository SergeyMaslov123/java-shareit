package ru.practicum.shareit.user.dto;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Value
@Validated
public class UserDto {
    Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 255)
    String name;
    @Email(groups = Marker.OnCreate.class)
    @NotEmpty(groups = Marker.OnCreate.class)
    @Size(max = 512)
    String email;
}
