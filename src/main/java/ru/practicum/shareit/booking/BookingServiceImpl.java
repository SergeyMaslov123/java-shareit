package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoAnswer addBooking(Long userId, BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getStart().isBlank() || bookingDto.getEnd().isBlank()) {
            throw new ValidationEx("start/end null");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (booking.getStart().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()) ||
                booking.getEnd().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()) ||
                booking.getStart().isAfter(booking.getEnd()) ||
                booking.getStart().equals(booking.getEnd())) {
            throw new ValidationEx("неправильные даты");
        }
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("User owner Item");
        }
        if (item.getAvailable()) {
            booking.setItem(item);
            booking.setBooker(user);
            booking.setStatus(Status.WAITING);
            return BookingMapper.toBookingDtoAnswer(bookingRepository.save(booking));

        } else {
            throw new ValidationEx("Item is busy");
        }
    }

    @Override
    public BookingDtoAnswer getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapper.toBookingDtoAnswer(booking);
        } else {
            throw new EntityNotFoundException("нет доступа к booking");
        }
    }

    @Override
    @Transactional
    public BookingDtoAnswer approvedBooking(Long userId, Long bookingId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationEx("status not waiting");
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (approved.equals("true")) {
                booking.setStatus(Status.APPROVED);
            } else if (approved.equals("false")) {
                booking.setStatus(Status.REJECTED);
            } else {
                throw new ValidationEx("Неверный approved");
            }
            bookingRepository.save(booking);
            return BookingMapper.toBookingDtoAnswer(booking);
        } else {
            throw new EntityNotFoundException("User не владелец Item");
        }
    }

    @Override
    public List<BookingDtoAnswer> getAllBookingsForUserId(Long userId, String state) {
        List<Booking> allBookingForUser = bookingRepository.getAllBookingsForUserId(userId);
        if (allBookingForUser.isEmpty()) {
            throw new EntityNotFoundException("User not booking");
        }
        System.out.println("all bookings for user " + allBookingForUser);
        switch (state) {
            case "ALL":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "PAST":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "WAITING":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "REJECTED":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "CURRENT":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())
                                && booking.getEnd().isAfter(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "FUTURE":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            default:
                throw new ValidationEx("Unknown state: " + state);

        }
    }

    @Override
    public List<BookingDtoAnswer> getAllBookingForUserOwner(Long userId, String state) {
        List<Booking> allBookingForUser = bookingRepository.getAllBookingsForUserItemId(userId);
        if (allBookingForUser.isEmpty()) {
            throw new EntityNotFoundException("User not booking");
        }
        System.out.println("all bookings for item " + allBookingForUser);
        switch (state) {
            case "ALL":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "PAST":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "WAITING":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStatus().equals(Status.WAITING))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "REJECTED":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStatus().equals(Status.REJECTED))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "CURRENT":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())
                                && booking.getEnd().isAfter(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            case "FUTURE":
                return allBookingForUser.stream()
                        .sorted((b1, b2) -> b2.getStart().compareTo(b1.getStart()))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()))
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
            default:
                throw new ValidationEx("Unknown state: " + state);
        }
    }
}

