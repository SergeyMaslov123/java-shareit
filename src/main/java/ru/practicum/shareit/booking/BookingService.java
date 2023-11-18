package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exception.Generated;

import javax.validation.Valid;
import java.util.List;

@Generated
public interface BookingService {
    BookingDtoAnswer addBooking(Long userId, @Valid BookingDtoRequest bookingDtoRequest);

    BookingDtoAnswer getBooking(Long bookingId, Long userId);

    BookingDtoAnswer approvedBooking(Long userId, Long bookingId, Boolean approved);

    List<BookingDtoAnswer> getAllBookingsForUserId(Long userId, String stateStr, Integer from, Integer size);

    List<BookingDtoAnswer> getAllBookingForUserOwner(Long userId, String state, Integer from, Integer size);
}
