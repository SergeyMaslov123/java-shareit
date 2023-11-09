package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserDto userDto) {
        return new User(null, userDto.getName(), userDto.getEmail());
    }

    public static UserDtoForBookingRequest toUserDtoFbr(User user) {
        return new UserDtoForBookingRequest(user.getId());
    }

}
