package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
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
    ItemRequest itemRequest1 = new ItemRequest(
            null,
            "descr1",
            user1,
            Instant.now()
    );
    ItemRequest itemRequest2 = new ItemRequest(
            null,
            "desc2",
            user2,
            Instant.now()
    );

    @BeforeEach
    private void addAll() {
        userRepository.save(user1);
        userRepository.save(user2);
        itemRequestRepository.save(itemRequest1);
        itemRequestRepository.save(itemRequest2);
    }

    @AfterEach
    private void deleteAll() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void findAllByRequestorIdOrderByCreated() {
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdOrderByCreated(1L);
        assertFalse(itemRequestList.isEmpty());
    }

    @Test
    void findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(0, 10, sort);
        Page<ItemRequest> pages = itemRequestRepository.findAll(page);

        Pageable page2 = PageRequest.of(0, 1, sort);
        Page<ItemRequest> pages2 = itemRequestRepository.findAll(page2);

        assertFalse(pages.isEmpty());
        assertEquals(pages.stream().count(), 2L);

        assertFalse(pages2.isEmpty());
        assertEquals(pages2.stream().count(), 1L);
    }
}