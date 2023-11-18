package ru.practicum.shareit.request;

import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswerThenCreate;

import javax.validation.Valid;
import java.util.List;

@Generated
public interface ItemRequestService {
    ItemRequestDtoAnswerThenCreate addItemRequest(@Valid ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoAnswer> getItemRequestForUser(Long userId);

    List<ItemRequestDtoAnswer> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoAnswer getItemRequestForId(Long userId, Long requestId);
}
