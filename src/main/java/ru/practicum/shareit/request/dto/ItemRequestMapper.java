package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd yyyy HH:mm:ss z").withLocale(Locale.ENGLISH);
        String date = formatter.format(ZonedDateTime
                .of(itemRequest.getCreated().atZone(ZoneOffset.UTC).toLocalDateTime(), ZoneId.of("GMT+3")));

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
                itemRequest.getCreated(),
                itemRequest.getRequestor(),
                allItem
        );
    }

    public static ItemRequestDtoForAll toItemRequestAll(ItemRequest itemRequest) {
        return new ItemRequestDtoForAll(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }
}
