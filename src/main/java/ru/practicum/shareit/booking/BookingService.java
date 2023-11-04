package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;

import java.util.List;

public interface BookingService {
    BookingDtoAnswer addBooking(Long userId, BookingDto bookingDto);

    BookingDtoAnswer getBooking(Long bookingId, Long userId);

    BookingDtoAnswer approvedBooking(Long userId, Long bookingId, String approved);

    List<BookingDtoAnswer> getAllBookingsForUserId(Long userId, String state);

    List<BookingDtoAnswer> getAllBookingForUserOwner(Long userId, String state);
}
