package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User addUser(User user);

    User getUser(Integer userId);

    User updateUser(Integer userId, User user);

    void deleteUser(Integer userId);

    List<User> allUser();
}
