package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.ZoneOffset;
import java.util.List;

@Generated
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                null,
                null);
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription());
    }

    @SneakyThrows
    public static ItemRequestDtoAnswerThenCreate toItemDtoAnswerThenCreate(ItemRequest itemRequest) {
        String date = itemRequest.getCreated().toString();
        return new ItemRequestDtoAnswerThenCreate(
                itemRequest.getId(),
                itemRequest.getDescription(),
                date,
                itemRequest.getRequestor()
        );
    }

    public static ItemRequestDtoAnswer toItemRequestDtoAnswer(ItemRequest itemRequest, List<ItemDto> allItem) {

        return new ItemRequestDtoAnswer(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().atZone(ZoneOffset.UTC).toInstant(),
                itemRequest.getRequestor(),
                allItem
        );
    }

    public static ItemRequestDtoForAll toItemRequestAll(ItemRequest itemRequest) {
        return new ItemRequestDtoForAll(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated().atZone(ZoneOffset.UTC).toInstant());
    }
}
