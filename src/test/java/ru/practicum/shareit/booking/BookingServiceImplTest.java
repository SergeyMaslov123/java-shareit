package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> argumentCaptor;
    long userId = 1L;
    long itemId = 1L;
    BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            itemId
    );
    User user = new User(
            1L,
            "Rob",
            "stark@mail.ru"
    );
    User user2 = new User(
            2L,
            "Rob2",
            "stark22@mail.ru"
    );
    Item item = new Item(
            1L,
            "item1",
            "desc1",
            Boolean.TRUE,
            user,
            null
    );
    Item item2 = new Item(
            2L,
            "item2",
            "desc2",
            Boolean.TRUE,
            user2,
            null
    );
    long bookingId = 1L;
    Boolean approved = true;
    Booking booking = new Booking(
            1L,
            Instant.now().minusSeconds(10000),
            Instant.now().minusSeconds(8000),
            item,
            user,
            Status.WAITING
    );
    Booking booking1 = new Booking(
            2L,
            Instant.now().plusSeconds(1000),
            Instant.now().plusSeconds(2000),
            item2,
            user2,
            Status.REJECTED
    );

    @Test
    void addBooking_whenItemAndUserFound_thenReturnBooking() {
        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        BookingDtoAnswer bookingDtoAnswer = BookingMapper.toBookingDtoAnswer(booking);
        when(itemRepository.findById(userId)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDtoAnswer actualBooking = bookingService.addBooking(2L, bookingDtoRequest);

        assertEquals(bookingDtoAnswer, actualBooking);
    }

    @Test
    void addBooking_whenUserAndItemNotFound_thenThrowEntityNotFoundEx() {
        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(userId, bookingDtoRequest));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addBooking_whenBookingStartIsAfterEnd_thenThrowValidationEx() {
        bookingDtoRequest = new BookingDtoRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().minusDays(2),
                itemId
        );
        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);

        assertThrows(ValidationEx.class, () -> bookingService.addBooking(userId, bookingDtoRequest));
        verify(bookingRepository, never()).save(booking);

    }

    @Test
    void addBooking_whenUserOwnerItem_thenThrowEntityNotFoundEx() {
        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(userId, bookingDtoRequest));
        verify(bookingRepository, never()).save(booking);

    }

    @Test
    void addBooking_whenItemAvailableFalse_thenThrowValidationEx() {
        item = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.FALSE,
                user,
                null
        );
        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(ValidationEx.class, () -> bookingService.addBooking(2L, bookingDtoRequest));
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void getBooking_whenBookingFound_thenReturnBooking() {

        item = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.FALSE,
                user,
                null
        );
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                item,
                user,
                Status.APPROVED
        );
        BookingDtoAnswer bookingDtoAnswer = BookingMapper.toBookingDtoAnswer(booking);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoAnswer actualBooking = bookingService.getBooking(bookingId, userId);

        assertEquals(bookingDtoAnswer, actualBooking);
    }

    @Test
    void getBooking_whenBookingNotFound_thenThrowEntityNotFoundEx() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, userId));
    }

    @Test
    void getBooking_whenUserNotBookerAndNotOwner_thenThrowEntityNotFoundEx() {
        long userId = 2L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(bookingId, userId));
    }

    @Test
    void approvedBooking_whenBookingFoundAndStatusWaitingAndUserOwnerItem_thenReturnBooking() {
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                item,
                user,
                Status.WAITING
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoAnswer actualBookingDtoAnswer = bookingService.approvedBooking(userId, bookingId, approved);

        verify(bookingRepository).save(argumentCaptor.capture());
        Booking actualBooking = argumentCaptor.getValue();

        assertEquals(booking.getId(), actualBookingDtoAnswer.getId());
        assertEquals(Status.APPROVED, actualBookingDtoAnswer.getStatus());
        assertEquals(item, actualBooking.getItem());
        assertEquals(user, actualBooking.getBooker());
        assertEquals(Status.APPROVED, actualBooking.getStatus());
    }

    @Test
    void approvedBooking_whenBookingFoundStatusWaitingUserOwnerApprovedFalse_thenReturnBookingStatusRejected() {
        approved = false;
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                item,
                user,
                Status.WAITING
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDtoAnswer actualBookingDtoAnswer = bookingService.approvedBooking(userId, bookingId, approved);

        verify(bookingRepository).save(argumentCaptor.capture());
        Booking actualBooking = argumentCaptor.getValue();

        assertEquals(booking.getId(), actualBookingDtoAnswer.getId());
        assertEquals(Status.REJECTED, actualBookingDtoAnswer.getStatus());
        assertEquals(item, actualBooking.getItem());
        assertEquals(user, actualBooking.getBooker());
        assertEquals(Status.REJECTED, actualBooking.getStatus());
    }

    @Test
    void approvedBooking_whenBookingNotFound_thenThrowEntityNotFoundEx() {
        Boolean approved = false;
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                null,
                null,
                Status.WAITING
        );

        assertThrows(EntityNotFoundException.class, () -> bookingService.approvedBooking(userId, bookingId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void approvedBooking_whenBookingFoundStatusNotWaiting_thenThrowValidationEx() {
        approved = false;
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                item,
                user,
                Status.APPROVED
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));


        assertThrows(ValidationEx.class, () -> bookingService.approvedBooking(userId, bookingId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void approvedBooking_whenBookingFoundStatusWaitingUserNotOwner_thenThrowEntityNotFoundEx() {
        approved = false;
        Booking booking = new Booking(
                1L,
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.UTC),
                item,
                user,
                Status.WAITING
        );
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));


        assertThrows(EntityNotFoundException.class, () -> bookingService.approvedBooking(2L, bookingId, approved));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStateAll_thenReturnListBookings() {
        String stateString = "ALL";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking, booking1)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking, booking1));
        when(bookingRepository.findAllByBooker_Id(userId, pageable)).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingsForUserId(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByBooker_IdAndStartIsAfter(any(), any(), any());

    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStatePAST_thenReturnListBookings() {
        String stateString = "PAST";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findAllByBooker_idAndEndIsBefore(eq(userId), any(), eq(pageable))).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingsForUserId(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findAllByBooker_Id(any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByBooker_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStateWAITING_thenReturnListBookings() {
        String stateString = "WAITING";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBooker_idAndStatus(userId, Status.WAITING, pageable)).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingsForUserId(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findAllByBooker_Id(any(), any());
        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByBooker_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStateREJECTED_thenReturnListBookings() {
        String stateString = "REJECTED";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBooker_idAndStatus(userId, Status.REJECTED, pageable)).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingsForUserId(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findAllByBooker_Id(any(), any());
        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByBooker_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStateCURRENT_thenReturnListBookings() {
        String stateString = "CURRENT";

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingsForUserId(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findAllByBooker_Id(any(), any());
        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStateFUTURE_thenReturnListBookings() {
        String stateString = "FUTURE";

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByBooker_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingsForUserId(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findAllByBooker_Id(any(), any());
        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsForUserId_whenBookingsFoundAndStateNot_thenThrowValidationEx() {
        String stateString = "NOT";

        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(userId, stateString, 1, 10));

        verify(bookingRepository, never()).findAllByBooker_Id(any(), any());
        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());

    }

    @Test
    void getAllBookingsForUserId_whenBookingsNotFound_thenThrowEntityNotFoundEx() {
        String stateString = "ALL";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);
        Page<Booking> pageBooking = new PageImpl<>(List.of());
        when(bookingRepository.findAllByBooker_Id(userId, pageable)).thenReturn(pageBooking);
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingsForUserId(userId, stateString, 1, 10));

        verify(bookingRepository, never()).findAllByBooker_idAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByBooker_idAndStatus(any(), any(), any());

    }

    @Test
    void getAllBookingForUserId_whenFromSizeNullAll_thenReturnBookings() {
        String stateString = "ALL";
        when(bookingRepository.findByBooker_IdOrderByStartDesc(userId)).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingsForUserId(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserId_whenFromSizeNullPast_thenReturnBookings() {
        String stateString = "PAST";
        when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(eq(userId), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingsForUserId(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserId_whenFromSizeNullWAITING_thenReturnBookings() {
        String stateString = "WAITING";
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingsForUserId(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserId_whenFromSizeNullREJECTED_thenReturnBookings() {
        String stateString = "REJECTED";
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingsForUserId(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserId_whenFromSizeNullCURRENT_thenReturnBookings() {
        String stateString = "CURRENT";
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingsForUserId(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserId_whenFromSizeNullFUTURE_thenReturnBookings() {
        String stateString = "FUTURE";
        when(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingsForUserId(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }


    @Test
    void getAllBookingsForUserId_whenFromSizeNotValid_thenThrowValidationEx() {
        String stateStringAll = "ALL";
        String stateStringPast = "PAST";
        String stateStringWAITING = "WAITING";
        String stateStringREJECTED = "REJECTED";
        String stateStringCURRENT = "CURRENT";
        String stateStringFUTURE = "FUTURE";

        int from = -1;
        int size = 10;

        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(1L, stateStringAll, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(1L, stateStringPast, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(1L, stateStringWAITING, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(1L, stateStringREJECTED, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(1L, stateStringCURRENT, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingsForUserId(1L, stateStringFUTURE, from, size));

    }

    @Test
    void getAllBookingForUserOwner_whenBookingFoundStateALL_thenReturnListBooking() {
        String stateString = "ALL";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking, booking1)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking, booking1));
        when(bookingRepository.findByItem_Owner_Id(userId, pageable)).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingForUserOwner_whenBookingFoundStatePAST_thenReturnListBooking() {
        String stateString = "PAST";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByItem_Owner_IdAndEndIsBefore(eq(userId), any(), eq(pageable))).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findByItem_Owner_Id(any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingForUserOwner_whenBookingFoundStateWAITING_thenReturnListBooking() {
        String stateString = "WAITING";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.WAITING, pageable)).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findByItem_Owner_Id(any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingForUserOwner_whenBookingFoundStateREJECTED_thenReturnListBooking() {
        String stateString = "REJECTED";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByItem_Owner_IdAndStatus(userId, Status.REJECTED, pageable)).thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findByItem_Owner_Id(any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingForUserOwner_whenBookingFoundStateCURRENT_thenReturnListBooking() {
        String stateString = "CURRENT";

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findByItem_Owner_Id(any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingForUserOwner_whenBookingFoundStateFUTURE_thenReturnListBooking() {
        String stateString = "FUTURE";

        List<BookingDtoAnswer> bookings = Stream.of(booking)
                .map(BookingMapper::toBookingDtoAnswer)
                .collect(Collectors.toList());
        Page<Booking> pageBooking = new PageImpl<>(List.of(booking));
        when(bookingRepository.findByItem_Owner_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(pageBooking);

        List<BookingDtoAnswer> actualListBookings = bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10);

        assertEquals(bookings.size(), actualListBookings.size());
        assertEquals(bookings, actualListBookings);
        verify(bookingRepository, never()).findByItem_Owner_Id(any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
    }

    @Test
    void getAllBookingsForOwnerId_whenBookingsFoundAndStateNot_thenThrowValidationEx() {
        String stateString = "NOT";

        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10));

        verify(bookingRepository, never()).findByItem_Owner_Id(any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());
    }

    @Test
    void getAllBookingsForOwnerId_whenBookingsNotFound_thenThrowEntityNotFoundEx() {
        String stateString = "ALL";
        Sort sort = Sort.by(Sort.Direction.DESC, "Start");
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, 10, sort);
        Page<Booking> pageBooking = new PageImpl<>(List.of());
        when(bookingRepository.findByItem_Owner_Id(userId, pageable)).thenReturn(pageBooking);
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.getAllBookingForUserOwner(userId, stateString, 1, 10));

        verify(bookingRepository, never()).findByItem_Owner_IdAndEndIsBefore(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStatus(any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(bookingRepository, never()).findByItem_Owner_IdAndStartIsAfter(any(), any(), any());

    }

    @Test
    void getAllBookingForUserOwner_whenFromSizeNullALL_thenReturnBookings() {
        String stateString = "ALL";
        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingForUserOwner(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserOwner_whenFromSizeNullPAST_thenReturnBookings() {
        String stateString = "PAST";
        when(bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingForUserOwner(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserOwner_whenFromSizeNullWAITING_thenReturnBookings() {
        String stateString = "WAITING";
        when(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingForUserOwner(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserOwner_whenFromSizeNullREJECTED_thenReturnBookings() {
        String stateString = "REJECTED";
        when(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingForUserOwner(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserOwner_whenFromSizeNullCURRENT_thenReturnBookings() {
        String stateString = "CURRENT";
        when(bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingForUserOwner(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserOwner_whenFromSizeNullFUTURE_thenReturnBookings() {
        String stateString = "FUTURE";
        when(bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoAnswer> bookings = bookingService.getAllBookingForUserOwner(userId, stateString, null, null);

        assertEquals(1, bookings.size());
    }

    @Test
    void getAllBookingForUserOwner_whenFromAndSizeNotValid_thenReturnValidationEx() {
        String stateStringAll = "ALL";
        String stateStringPast = "PAST";
        String stateStringWAITING = "WAITING";
        String stateStringREJECTED = "REJECTED";
        String stateStringCURRENT = "CURRENT";
        String stateStringFUTURE = "FUTURE";

        int from = -1;
        int size = 10;

        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(1L, stateStringAll, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(1L, stateStringPast, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(1L, stateStringWAITING, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(1L, stateStringREJECTED, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(1L, stateStringCURRENT, from, size));
        assertThrows(ValidationEx.class, () -> bookingService.getAllBookingForUserOwner(1L, stateStringFUTURE, from, size));

    }
}