package ru.practicum.shareit.booking.dto;

import lombok.Value;
import ru.practicum.shareit.exception.Generated;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
@Generated
public class BookingDtoRequest {
    Long id;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
    Long itemId;

}
