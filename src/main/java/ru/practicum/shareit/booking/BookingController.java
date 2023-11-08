package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.item.HeaderName;

import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoAnswer addBooking(@RequestHeader(HeaderName.header) Long userId,
                                       @RequestBody BookingDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoAnswer approvedBooking(@RequestHeader(HeaderName.header) Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved) {
        return bookingService.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoAnswer getBooking(@RequestHeader(HeaderName.header) Long userId,
                                       @PathVariable Long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoAnswer> getAllBookingForUserId(@RequestHeader(HeaderName.header) Long userId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsForUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoAnswer> getAllBookingsForUserItems(@RequestHeader(HeaderName.header) Long userId,
                                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingForUserOwner(userId, state);
    }


}
