package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Generated
public class ValidationEx extends RuntimeException {
    public ValidationEx(String message) {
        super(message);
    }
}
