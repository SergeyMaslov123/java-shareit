package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CommentsRepositoryTest {
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
            1L,
            "user1",
            "email@mail.ru"
    );
    User user2 = new User(
            2L,
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

    @BeforeEach
    private void addAll() {
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
    private void deleteAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
        commentsRepository.deleteAll();
    }


    @Test
    void findByItemId() {
        List<Comment> comments = commentsRepository.findByItemId(1L);

        assertFalse(comments.isEmpty());
        assertEquals(1, comments.size());
        assertEquals("comment1", comments.get(0).getText());
    }

    @Test
    void findAllByItemIdInOrderByCreatedAsc() {
        List<Comment> comments = commentsRepository.findAllByItemIdInOrderByCreatedAsc(Set.of(1L, 2L));

        assertEquals(2, comments.size());
        assertEquals("comment1", comments.get(0).getText());
        assertEquals("comment2", comments.get(1).getText());
    }
}