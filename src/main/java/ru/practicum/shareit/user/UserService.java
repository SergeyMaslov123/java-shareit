package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User addUser(User user);

    User getUser(Long userId);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);

    List<User> allUser();
}
