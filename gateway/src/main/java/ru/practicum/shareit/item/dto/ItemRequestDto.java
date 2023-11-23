package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.MarkerGeteway;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = MarkerGeteway.OnCreate.class)
    @Size(max = 255, groups = {MarkerGeteway.OnCreate.class, MarkerGeteway.OnUpdate.class})
    String name;
    @Size(max = 255, groups = {MarkerGeteway.OnCreate.class, MarkerGeteway.OnUpdate.class})
    @NotBlank(groups = MarkerGeteway.OnCreate.class)
    String description;
    @NotNull(groups = MarkerGeteway.OnCreate.class)
    Boolean available;
    Long requestId;
}
