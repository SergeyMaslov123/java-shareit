package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.user.dto.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private long generatedIdUser = 1L;

    @Override
    @Transactional
    @Validated({Marker.OnCreate.class})
    public UserDto addUser(@Valid UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(generatedIdUser);
        generatedIdUser++;
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found")));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        User user = UserMapper.toUser(userDto);
        if (user.getEmail() != null && user.getName() == null) {
            validEmail(user);
            user.setName(oldUser.getName());
        } else if (user.getName() != null && user.getEmail() == null) {
            validName(user);
            user.setEmail(oldUser.getEmail());
        } else if (user.getName() != null) {
            validName(user);
            validEmail(user);
        } else {
            throw new ValidationEx("email/ name is empty");
        }
        user.setId(userId);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> allUser() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void validEmail(User user) {
        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || user.getEmail().isEmpty()
                || !user.getEmail().contains("@")) {
            throw new ValidationEx("Неверный email");
        }
    }

    private void validName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            throw new ValidationEx("name is empty");
        }
    }
}
