package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDtoForBookingRequest;
import ru.practicum.shareit.user.dto.UserDtoForBookingRequest;

import java.time.LocalDateTime;

@Value
public class BookingDtoAnswer {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    ItemDtoForBookingRequest item;
    UserDtoForBookingRequest booker;
    Status status;
}
