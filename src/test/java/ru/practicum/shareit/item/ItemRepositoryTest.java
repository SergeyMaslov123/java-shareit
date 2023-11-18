package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryTest {
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

    @BeforeEach
    private void addAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRepository.save(item1);
        itemRepository.save(item2);
    }

    @AfterEach
    private void deleteAll() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }


    @Test
    void findByOwnerId() {
        List<Item> items = itemRepository.findByOwnerId(1L);

        assertFalse(items.isEmpty());
        assertEquals(items.size(), 1);
        assertEquals("item1", items.get(0).getName());
    }

    @Test
    void searchItem() {
        List<Item> items = itemRepository.searchItem("sc2");
        assertEquals(1, items.size());
        assertEquals("item2", items.get(0).getName());
    }


    @Test
    void findAllByRequestId() {
        ItemRequest itemRequest1 = new ItemRequest(
                null,
                "descr1",
                user1,
                LocalDateTime.now()
        );
        itemRequestRepository.save(itemRequest1);
        item2.setRequest(itemRequest1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAllByRequestId(1L);

        assertEquals(1, items.size());
        assertEquals("item2", items.get(0).getName());
    }

    @Test
    void findAllByRequestIdIn() {
        ItemRequest itemRequest1 = new ItemRequest(
                null,
                "descr1",
                user1,
                LocalDateTime.now()
        );
        itemRequestRepository.save(itemRequest1);
        item2.setRequest(itemRequest1);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAllByRequestIdIn(Set.of(1L));
        assertEquals(1, items.size());
        assertEquals("item2", items.get(0).getName());
    }

    @Test
    void findAllByOwnerId() {
        Pageable page = PageRequest.of(0, 10);

        Page<Item> pages = itemRepository.findAllByOwnerId(1L, page);

        assertEquals(pages.stream().count(), 1L);
        assertEquals("item1", pages.stream().collect(Collectors.toList()).get(0).getName());
    }

    @Test
    void searchItemPage() {
        Pageable page = PageRequest.of(0, 10);

        Page<Item> pages = itemRepository.searchItemPage("sc1", page);

        assertEquals(1L, pages.stream().count());
        assertEquals("item1", pages.stream().collect(Collectors.toList()).get(0).getName());

    }
}