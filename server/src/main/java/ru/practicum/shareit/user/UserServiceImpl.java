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
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(generatedIdUser);
        generatedIdUser++;
        User user1 = userRepository.save(user);
        System.out.println(user1);
        return UserMapper.toUserDto(user1);
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user  not found")));
    }

    @Override
    @Transactional
    @Validated({Marker.OnUpdate.class})
    public UserDto updateUser(Long userId, UserDto userDto) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
        if (userDto.getName() == null && userDto.getEmail() == null) {
            throw new ValidationEx("email, name null");
        }
        User user = UserMapper.toUser(userDto);
        String name = user.getName();
        String email = user.getEmail();
        if (name == null || name.isBlank()) {
            user.setName(oldUser.getName());
        } else if (email == null || email.isBlank()) {
            user.setEmail(oldUser.getEmail());
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
}
