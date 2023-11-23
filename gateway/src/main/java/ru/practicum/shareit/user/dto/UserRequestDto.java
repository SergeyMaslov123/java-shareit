package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.MarkerGeteway;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private Long id;
    @NotBlank(groups = MarkerGeteway.OnCreate.class)
    @Size(max = 255, groups = {MarkerGeteway.OnCreate.class, MarkerGeteway.OnUpdate.class})
    private String name;
    @Email(groups = {MarkerGeteway.OnCreate.class, MarkerGeteway.OnUpdate.class})
    @NotEmpty(groups = MarkerGeteway.OnCreate.class)
    @Size(max = 512, groups = {MarkerGeteway.OnCreate.class, MarkerGeteway.OnUpdate.class})
    private String email;
}
