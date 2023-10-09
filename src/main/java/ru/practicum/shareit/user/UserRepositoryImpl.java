package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictEx;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class UserRepositoryImpl implements UserRepository {
    private Map<Integer, User> allUsers = new HashMap<>();
    private static int id = 1;

    @Override
    public User addUser(User user) {
        validEmail(user);
        validName(user);
        user.setId(id);
        id = id + 1;
        allUsers.put(user.getId(), user);
        return user;

    }

    @Override
    public User getUser(Integer id) {
        if (allUsers.containsKey(id)) {
            return allUsers.get(id);
        } else {
            throw new NotFoundException("user not found");
        }
    }

    @Override
    public User updateUser(Integer userId, User user) {
        if (allUsers.containsKey(userId)) {
            user.setId(userId);
            if (user.getEmail() != null && user.getName() == null) {
                if (!allUsers.get(userId).getEmail().equals(user.getEmail())) {
                    validEmail(user);
                }
                user.setName(allUsers.get(userId).getName());
            } else if (user.getName() != null && user.getEmail() == null) {
                validName(user);
                user.setEmail(allUsers.get(userId).getEmail());
            } else if (user.getName() != null && user.getEmail() != null) {
                if (!allUsers.get(userId).getEmail().equals(user.getEmail())) {
                    validEmail(user);
                }
            } else {
                throw new ValidationEx("name null, email null");
            }
            allUsers.put(userId, user);
            return user;
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        if (allUsers.containsKey(userId)) {
            allUsers.remove(userId);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public List<User> allUser() {
        return new ArrayList<>(allUsers.values());
    }

    private Integer getId() {
        int lastId = allUsers.values()
                .stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    private void validEmail(User user) {
        if (user.getEmail() == null
                || user.getEmail().isBlank()
                || user.getEmail().isEmpty()
                || !user.getEmail().contains("@")) {
            throw new ValidationEx("Неверный email");
        } else if (allUsers.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new ConflictEx("email is busy");
        }
    }

    private void validName(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            throw new ValidationEx("name is empty");
        }
    }
}
