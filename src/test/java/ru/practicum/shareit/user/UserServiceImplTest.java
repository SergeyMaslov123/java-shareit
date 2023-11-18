package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void addUser_valid_param() {
        UserDto userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        User userToSave = UserMapper.toUser(userDto);
        userToSave.setId(1L);
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.addUser(userDto);

        assertEquals(userDto, actualUserDto);
        verify(userRepository).save(userToSave);
    }

    @Test
    void getUserById_whenUserFound_thenReturnUser() {
        UserDto userDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        User userToSave = UserMapper.toUser(userDto);
        Long userId = 1L;
        userToSave.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToSave));

        UserDto actualUserDto = userService.getUser(userId);

        assertEquals(userDto, actualUserDto);
    }

    @Test
    void getUserById_whenUserNotFound_thenEntityNotFoundEx() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    void updateUser_whenParamNewUserIsValidName_thenReturnNewUser() {
        UserDto oldUserDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        UserDto newUserDto = new UserDto(
                1L,
                "Snow",
                "john.doe@mail.com"
        );
        UserDto requestUser = new UserDto(
                1L,
                "Snow",
                null);
        Long userId = 1L;
        User oldUser = UserMapper.toUser(oldUserDto);
        oldUser.setId(userId);
        User newUser = UserMapper.toUser(newUserDto);
        newUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(newUser)).thenReturn(newUser);

        UserDto actualUser = userService.updateUser(userId, requestUser);

        verify(userRepository).save(newUser);
        verify(userRepository).save(userArgumentCaptor.capture());
        User userValue = userArgumentCaptor.getValue();

        assertEquals(actualUser, newUserDto);
        assertEquals("Snow", userValue.getName());
        assertEquals("john.doe@mail.com", userValue.getEmail());
        assertEquals(userId, userValue.getId());
    }

    @Test
    void updateUser_whenParamNewUserIsValidEmail_thenReturnNewUser() {
        UserDto oldUserDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        UserDto newUserDto = new UserDto(
                1L,
                "John",
                "snow.doe@mail.com"
        );
        UserDto requestUser = new UserDto(
                1L,
                null,
                "snow.doe@mail.com");
        Long userId = 1L;
        User oldUser = UserMapper.toUser(oldUserDto);
        oldUser.setId(userId);
        User newUser = UserMapper.toUser(newUserDto);
        newUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(newUser)).thenReturn(newUser);

        UserDto actualUser = userService.updateUser(userId, requestUser);

        verify(userRepository).save(newUser);
        verify(userRepository).save(userArgumentCaptor.capture());
        User userValue = userArgumentCaptor.getValue();

        assertEquals(actualUser, newUserDto);
        assertEquals("John", userValue.getName());
        assertEquals("snow.doe@mail.com", userValue.getEmail());
        assertEquals(userId, userValue.getId());
    }

    @Test
    void updateUser_whenParamNewUserIsValidAll_thenReturnNewUser() {
        UserDto oldUserDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        UserDto newUserDto = new UserDto(
                1L,
                "Rob",
                "stark.doe@mail.com"
        );
        UserDto requestUser = new UserDto(
                1L,
                "Rob",
                "stark.doe@mail.com"
        );
        Long userId = 1L;
        User oldUser = UserMapper.toUser(oldUserDto);
        oldUser.setId(userId);
        User newUser = UserMapper.toUser(newUserDto);
        newUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(newUser)).thenReturn(newUser);

        UserDto actualUser = userService.updateUser(userId, requestUser);

        assertEquals(actualUser, newUserDto);
        verify(userRepository).save(newUser);
    }

    @Test
    void allUser_whenUsersFound_thenReturnUsers() {
        UserDto userDto1 = new UserDto(
                1L,
                "Rob",
                "stark.doe@mail.com"
        );
        UserDto userDto2 = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        List<UserDto> allUsersDto = List.of(userDto1, userDto2);
        User user1 = UserMapper.toUser(userDto1);
        User user2 = UserMapper.toUser(userDto2);
        user1.setId(1L);
        user2.setId(2L);
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> actualUsersDto = userService.allUser();

        assertEquals(allUsersDto.size(), actualUsersDto.size());
    }

    @Test
    void deleteUser() {
        userRepository.deleteById(1L);
    }

    @Test
    void updateUser_whenParamNewUserIsValidAllNot_thenReturnNewUser() {
        UserDto oldUserDto = new UserDto(
                1L,
                "John",
                "john.doe@mail.com"
        );
        UserDto newUserDto = new UserDto(
                1L,
                null,
                null
        );

        Long userId = 1L;
        User oldUser = UserMapper.toUser(oldUserDto);
        oldUser.setId(userId);
        User newUser = UserMapper.toUser(newUserDto);
        newUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));

        assertThrows(ValidationEx.class, () -> userService.updateUser(userId, newUserDto));

    }
}
