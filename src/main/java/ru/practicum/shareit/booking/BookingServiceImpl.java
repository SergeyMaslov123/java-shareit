package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Validated
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoAnswer addBooking(Long userId, @Valid BookingDtoRequest bookingDtoRequest) {
        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);
        if (booking.getStart().isAfter(booking.getEnd()) ||
                booking.getStart().equals(booking.getEnd())) {
            throw new ValidationEx("неправильные даты");
        }
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
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
    public BookingDtoAnswer approvedBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationEx("status not waiting");
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            bookingRepository.save(booking);
            return BookingMapper.toBookingDtoAnswer(booking);
        } else {
            throw new EntityNotFoundException("User не владелец Item");
        }
    }

    @Override
    public List<BookingDtoAnswer> getAllBookingsForUserId(Long userId, String stateStr, Integer from, Integer size) {
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException ex) {
            throw new ValidationEx("Unknown state: " + stateStr);
        }
        List<BookingDtoAnswer> allBookings;
        switch (state) {
            case ALL:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findAllByBooker_Id(userId, pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                allBookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
                }
                break;

            case PAST:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findAllByBooker_idAndEndIsBefore(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                allBookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(
                                userId,
                                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                        )
                        .stream()
                        .map(BookingMapper::toBookingDtoAnswer)
                        .collect(Collectors.toList());
                }
                break;
            case WAITING:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByBooker_idAndStatus(userId, Status.WAITING, pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;

            case REJECTED:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByBooker_idAndStatus(userId, Status.REJECTED, pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case CURRENT:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByBooker_idAndStartIsBeforeAndEndIsAfter(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                            ).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case FUTURE:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByBooker_IdAndStartIsAfter(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                            ).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            default:
                throw new ValidationEx("Unknown state: " + state);
        }
        if (allBookings.isEmpty()) {
            throw new EntityNotFoundException("User not booking");
        } else {
            return allBookings;
        }
    }

    @Override
    public List<BookingDtoAnswer> getAllBookingForUserOwner(Long userId, String stateStr, Integer from, Integer size) {
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (IllegalArgumentException ex) {
            throw new ValidationEx("Unknown state: " + stateStr);

        }
        List<BookingDtoAnswer> allBookings;

        switch (state) {
            case ALL:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByItem_Owner_Id(userId, pageable).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(userId).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case PAST:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    pageable)
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                            )
                            .stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case WAITING:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, pageable).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.WAITING).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case REJECTED:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, pageable).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(userId, Status.REJECTED).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case CURRENT:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    pageable
                            ).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                            ).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            case FUTURE:
                if(from!= null && size != null) {
                    if (from < 0 || size < 0) {
                        throw new ValidationEx("некорректный запрос from, size");
                    }
                    Sort sort = Sort.by(Sort.Direction.DESC, "Start");
                    Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size, sort);
                    allBookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant(),
                                    pageable
                            ).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                } else {
                    allBookings = bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(
                                    userId,
                                    LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                            ).stream()
                            .map(BookingMapper::toBookingDtoAnswer)
                            .collect(Collectors.toList());
                }
                break;
            default:
                throw new ValidationEx("Unknown state: " + state);
        }
        if (allBookings.isEmpty()) {
            throw new EntityNotFoundException("User not booking");
        } else {
            return allBookings;
        }
    }
}

