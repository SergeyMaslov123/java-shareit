package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;

import javax.validation.Valid;
import java.util.List;

public interface BookingService {
    BookingDtoAnswer addBooking(Long userId, BookingDto bookingDto);

    BookingDtoAnswer getBooking(Long bookingId, Long userId);

    BookingDtoAnswer approvedBooking(Long userId, Long bookingId, String approved);

    List<BookingDtoAnswer> getAllBookingsForUserId(Long userId, String stateStr);

    List<BookingDtoAnswer> getAllBookingForUserOwner(Long userId, String state);
}
