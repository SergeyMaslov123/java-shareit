package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User getUser(Integer id);

    User updateUser(Integer userId, User user);

    void deleteUser(Integer userId);

    List<User> allUser();
}
