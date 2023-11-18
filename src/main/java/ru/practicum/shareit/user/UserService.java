package ru.practicum.shareit.user;

import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Generated
public interface UserService {
    UserDto addUser(@Valid UserDto userDto);

    UserDto getUser(Long userId);

    UserDto updateUser(Long userId, @Valid UserDto userDto);

    void deleteUser(Long userId);

    List<UserDto> allUser();
}
