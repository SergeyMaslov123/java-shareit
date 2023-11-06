package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.Status;


@Value
public class BookingDto {
    Long id;
    String start;
    String end;
    Long itemId;
    Long bookerId;
    Status status;
}
