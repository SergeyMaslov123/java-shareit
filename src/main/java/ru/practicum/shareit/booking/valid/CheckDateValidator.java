package ru.practicum.shareit.booking.valid;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, BookingDto> {

    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        Instant start = LocalDateTime.parse(bookingDto.getStart()).atZone(ZoneOffset.UTC).toInstant();
        Instant end = LocalDateTime.parse(bookingDto.getEnd()).atZone(ZoneOffset.UTC).toInstant();
        boolean check = true;
        if (start == null || end == null) {
            check = false;
        } else if (start.isAfter(end)) {
            check = false;
        } else if (start.isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())) {
            check = false;
        }
        return check;
    }
}
