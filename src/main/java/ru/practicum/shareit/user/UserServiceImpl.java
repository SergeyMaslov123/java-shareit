package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private static long generatedIdUser = 1L;

    @Override
    @Transactional
    public User addUser(User user) {
        validEmail(user);
        validName(user);
        user.setId(generatedIdUser);
        generatedIdUser++;
        return userRepository.save(user);
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User user) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));
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
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> allUser() {
        return userRepository.findAll();
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
