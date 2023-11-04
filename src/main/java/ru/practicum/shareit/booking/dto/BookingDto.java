package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.Status;


/**
 * TODO Sprint add-bookings.
 */
@Value
public class BookingDto {
    Long id;
    String start;
    String end;
    Long itemId;
    Long bookerId;
    Status status;
}
