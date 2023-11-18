package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.Generated;

import java.time.LocalDateTime;


@Value
@Generated
public class BookingDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    Status status;
}
