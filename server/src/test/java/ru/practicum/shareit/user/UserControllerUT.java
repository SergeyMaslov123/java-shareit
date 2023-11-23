package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerUT {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;


    @Test
    void addUser() {
        UserDto userDto = new UserDto(1L,
                "John",
                "john.doe@mail.com");
        when(userService.addUser(userDto)).thenReturn(userDto);

        UserDto actualUser = userController.addUser(userDto);

        assertEquals(userDto, actualUser);
    }

    @Test
    void getUser() {
        UserDto userDto = new UserDto(1L,
                "John",
                "john.doe@mail.com");
        Long userId = 1L;
        when(userService.getUser(userId)).thenReturn(userDto);

        UserDto actualUser = userController.getUser(userId);

        assertEquals(actualUser, userDto);
    }

    @Test
    void getAllUsers() {
        UserDto userDto1 = new UserDto(1L,
                "John1",
                "john.doe1@mail.com");
        UserDto userDto2 = new UserDto(2L,
                "John2",
                "john.doe2@mail.com");
        List<UserDto> users = List.of(userDto1, userDto2);
        when(userService.allUser()).thenReturn(users);

        List<UserDto> actualUser = userController.getAllUsers();

        assertEquals(users.size(), actualUser.size());
        assertEquals(users, actualUser);

    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto(1L,
                "John",
                "john.doe@mail.com");
        Long userId = 1L;
        when(userService.updateUser(userId, userDto)).thenReturn(userDto);

        UserDto actualUser = userController.updateUser(userId, userDto);

        assertEquals(userDto, actualUser);
    }
}