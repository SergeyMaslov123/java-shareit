package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.MarkerGeteway;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Validated
public class UserControllerGateway {
    private final UserClient userClient;

    @PostMapping
    @Validated({MarkerGeteway.OnCreate.class})
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserRequestDto userDto) {
        System.out.println("add User gateway");
        return userClient.addUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    @Validated({MarkerGeteway.OnUpdate.class})
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Valid @RequestBody UserRequestDto userDto) {
        return userClient.updateUser(userId, userDto);
    }

}