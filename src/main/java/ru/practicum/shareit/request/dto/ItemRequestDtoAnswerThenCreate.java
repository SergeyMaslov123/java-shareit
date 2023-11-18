package ru.practicum.shareit.request.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.user.User;

@Value
@Generated
public class ItemRequestDtoAnswerThenCreate {
    long id;
    String description;
    String created;
    User requestor;
}
