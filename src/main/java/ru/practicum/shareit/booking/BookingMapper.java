package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        LocalDateTime endDate = booking.getEnd().atZone(ZoneOffset.UTC).toLocalDateTime();
        LocalDateTime startDate = booking.getStart().atZone(ZoneOffset.UTC).toLocalDateTime();
        return new BookingDto(booking.getId(),
                startDate,
                endDate,
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Instant startDate = bookingDto.getStart().atZone(ZoneOffset.UTC).toInstant();
        Instant endDate = bookingDto.getEnd().atZone(ZoneOffset.UTC).toInstant();
        return new Booking(
                bookingDto.getId(),
                startDate,
                endDate,
                null,
                null,
                bookingDto.getStatus());
    }

    public static BookingDtoAnswer toBookingDtoAnswer(Booking booking) {
        return new BookingDtoAnswer(booking.getId(),
                LocalDateTime.ofInstant(booking.getStart(), ZoneOffset.UTC),
                LocalDateTime.ofInstant(booking.getEnd(), ZoneOffset.UTC),
                ItemMapper.toItemDtoForBR(booking.getItem()),
                UserMapper.toUserDtoFbr(booking.getBooker()),
                booking.getStatus());
    }

}
