package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.CommentsRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private CommentsRepository commentsRepository;
    @Autowired
    private BookingRepository bookingRepository;
    User user1 = new User(
            null,
            "user1",
            "email@mail.ru"
    );
    User user2 = new User(
            null,
            "user2",
            "email2@mail.ru"
    );
    Item item1 = new Item(
            null,
            "item1",
            "desc1",
            true,
            user1,
            null
    );
    Item item2 = new Item(
            null,
            "item2",
            "desc2",
            true,
            user2,
            null
    );
    Booking booking1 = new Booking(
            null,
            Instant.now().minusSeconds(1000),
            Instant.now().minusSeconds(500),
            item1,
            user2,
            Status.APPROVED
    );
    Booking booking2 = new Booking(
            null,
            Instant.now().minusSeconds(1000),
            Instant.now().minusSeconds(500),
            item2,
            user1,
            Status.APPROVED
    );
    Comment comment = new Comment(
            null,
            "comment1",
            item1,
            user2,
            Instant.now()
    );
    Comment comment2 = new Comment(
            null,
            "comment2",
            item2,
            user1,
            Instant.now()
    );
    Pageable pageable = PageRequest.of(0, 10);
    Instant time = Instant.now();

    @BeforeEach
    public void addAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        commentsRepository.save(comment);
        commentsRepository.save(comment2);
    }
    @AfterEach
    public void deleteAll() {
        System.out.println("delete");
        itemRequestRepository.deleteAll();
        commentsRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }




    @Test
    void findByBooker_IdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(1L);

        assertEquals(1, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
    }

    @Test
    void findAllByBooker_Id() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Booking> pageBookings = bookingRepository.findAllByBooker_Id(1L, pageable);
        assertEquals(1L, pageBookings.stream().count());
        assertEquals(item2, pageBookings.stream().collect(Collectors.toList()).get(0).getItem());

    }

    @Test
    void findByBooker_IdAndEndIsBeforeOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now(),
                Instant.now().plusSeconds(1000),
                item2,
                user1,
                Status.APPROVED
        );
        bookingRepository.save(booking3);

        Instant end = Instant.now();
        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(1L, end);

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
    }

    @Test
    void findAllByBooker_idAndEndIsBefore() {
        Booking booking3 = new Booking(
                null,
                Instant.now(),
                Instant.now().plusSeconds(1000),
                item2,
                user1,
                Status.APPROVED
        );
        bookingRepository.save(booking3);
        Pageable pageable = PageRequest.of(0, 10);
        Instant end = Instant.now();
        Page<Booking> bookings = bookingRepository.findAllByBooker_idAndEndIsBefore(1L, end, pageable);

        assertEquals(1L, bookings.stream().count());
        assertEquals(item2, bookings.stream().collect(Collectors.toList()).get(0).getItem());
    }

    @Test
    void findByBooker_IdAndStatusOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now(),
                Instant.now().plusSeconds(1000),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        List<Booking> bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(1L, Status.REJECTED);
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByBooker_idAndStatus() {
        Booking booking3 = new Booking(
                null,
                Instant.now(),
                Instant.now().plusSeconds(1000),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = bookingRepository.findByBooker_idAndStatus(1L, Status.REJECTED, pageable);
        List<Booking> bookings = bookingsPage.toList();
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartAsc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(1000),
                Instant.now().plusSeconds(1000),
                item2,
                user1,
                Status.REJECTED
        );
        Instant time = Instant.now();
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(1L, time, time);
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByBooker_idAndStartIsBeforeAndEndIsAfter() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(1000),
                Instant.now().plusSeconds(1000),
                item2,
                user1,
                Status.REJECTED
        );
        Instant time = Instant.now();
        bookingRepository.save(booking3);
        Page<Booking> bookingPage = bookingRepository.findByBooker_idAndStartIsBeforeAndEndIsAfter(1L, time, time, pageable);
        List<Booking> bookings = bookingPage.toList();
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStartIsAfterOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        Instant time = Instant.now();
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(1L, time);

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByBooker_IdAndStartIsAfter() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        Instant time = Instant.now();
        bookingRepository.save(booking3);

        Page<Booking> bookingPage = bookingRepository.findByBooker_IdAndStartIsAfter(1L, time, pageable);
        List<Booking> bookings = bookingPage.toList();
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(1L);

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_Id() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        Page<Booking> bookingPage = bookingRepository.findByItem_Owner_Id(1L, pageable);
        List<Booking> bookings = bookingPage.toList();
        assertEquals(1L, bookings.size());
        assertEquals(item1, bookings.get(0).getItem());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        Instant time = Instant.now();

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(1L, time);

        assertEquals(1L, bookings.size());
        assertEquals(item1, bookings.get(0).getItem());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndEndIsBefore() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        Instant time = Instant.now();
        Page<Booking> bookingPage = bookingRepository.findByItem_Owner_IdAndEndIsBefore(1L, time, pageable);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1L, bookings.size());
        assertEquals(item1, bookings.get(0).getItem());
        assertEquals(1L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndStatusOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(2L, Status.REJECTED);

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndStatus() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        Page<Booking> bookingPage = bookingRepository.findByItem_Owner_IdAndStatus(2L, Status.REJECTED, pageable);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(2L, time, time);
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        Page<Booking> bookingPage = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(2L, time, time, pageable);

        List<Booking> bookings = bookingPage.toList();
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndStartIsAfterOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(2L, time);

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findByItem_Owner_IdAndStartIsAfter() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(1000),
                Instant.now().plusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);
        Page<Booking> bookingPage = bookingRepository.findByItem_Owner_IdAndStartIsAfter(2L, time, pageable);
        List<Booking> bookings = bookingPage.toList();

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(2000),
                Instant.now().minusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        boolean actual = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore(1L, 2L, Status.REJECTED, time);

        assertTrue(actual);
    }

    @Test
    void findAllByItem_IdInAndStatusAndStartLessThanEqualOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(2000),
                Instant.now().minusSeconds(1500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository
                .findAllByItem_IdInAndStatusAndStartLessThanEqualOrderByStartDesc(Set.of(1L, 2L), Status.REJECTED, time);

        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findAllByItem_IdInAndStatusAndStartIsAfterOrderByStartAsc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(2000),
                Instant.now().plusSeconds(2500),
                item2,
                user1,
                Status.REJECTED
        );
        bookingRepository.save(booking3);

        List<Booking> bookings = bookingRepository
                .findAllByItem_IdInAndStatusAndStartIsAfterOrderByStartAsc(Set.of(1L, 2L), Status.REJECTED, time);
        assertEquals(1L, bookings.size());
        assertEquals(item2, bookings.get(0).getItem());
        assertEquals(3L, bookings.get(0).getId());
    }

    @Test
    void findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByStartDesc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().minusSeconds(2000),
                Instant.now().minusSeconds(1500),
                item2,
                user1,
                Status.APPROVED
        );
        bookingRepository.save(booking3);

        Booking booking = bookingRepository
                .findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByStartDesc(2L, Status.APPROVED, time);

        assertEquals(2L, booking.getId());
    }

    @Test
    void findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc() {
        Booking booking3 = new Booking(
                null,
                Instant.now().plusSeconds(2000),
                Instant.now().plusSeconds(12500),
                item2,
                user1,
                Status.APPROVED
        );
        bookingRepository.save(booking3);

        Booking booking = bookingRepository
                .findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(2L, Status.APPROVED, time);

        assertEquals(3L, booking.getId());
    }

}