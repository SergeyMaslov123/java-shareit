package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    @Override
    public User getUser(Integer userId) {
        return userRepository.getUser(userId);
    }

    @Override
    public User updateUser(Integer userId, User user) {
        return userRepository.updateUser(userId, user);
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteUser(userId);
    }

    @Override
    public List<User> allUser() {
        return userRepository.allUser();
    }
}
