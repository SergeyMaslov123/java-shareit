package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;

import javax.validation.Valid;
import java.util.List;

@Validated
public interface BookingService {
    BookingDtoAnswer addBooking(Long userId, @Valid BookingDto bookingDto);

    BookingDtoAnswer getBooking(Long bookingId, Long userId);

    BookingDtoAnswer approvedBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDtoAnswer> getAllBookingsForUserId(Long userId, String stateStr);

    List<BookingDtoAnswer> getAllBookingForUserOwner(Long userId, String state);
}
