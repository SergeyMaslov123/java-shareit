package ru.practicum.shareit.booking.dto;

import lombok.Value;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Value
@Validated
public class BookingDto {
    Long id;
    @FutureOrPresent
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
    Long itemId;
    Long bookerId;
    Status status;
}
