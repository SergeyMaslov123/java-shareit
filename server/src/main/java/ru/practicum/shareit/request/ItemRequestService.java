package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswer;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswerThenCreate;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoAnswerThenCreate addItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDtoAnswer> getItemRequestForUser(Long userId);

    List<ItemRequestDtoAnswer> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoAnswer getItemRequestForId(Long userId, Long requestId);
}
