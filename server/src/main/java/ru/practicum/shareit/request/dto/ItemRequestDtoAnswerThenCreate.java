package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.user.User;

@Value
public class ItemRequestDtoAnswerThenCreate {
    long id;
    String description;
    String created;
    User requestor;
}
