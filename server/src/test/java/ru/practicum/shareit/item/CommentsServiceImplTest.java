package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentsServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @InjectMocks
    private CommentsServiceImpl commentsService;


    @Test
    void addComments_whenItemAndUserFoundAndUserHaveBooking_returnComment() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item item = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Comment comment = new Comment(
                1L,
                "text",
                item,
                user,
                Instant.now()
        );
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest(1L, "text");
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore(eq(1L), eq(1L), eq(Status.APPROVED), any())
        ).thenReturn(Boolean.TRUE);
        when(commentsRepository.save(any())).thenReturn(comment);
        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        CommentDto actualCommentDto = commentsService.addComments(commentDtoRequest, itemId, userId);

        assertEquals(commentDto, actualCommentDto);
    }

    @Test
    void addComments_whenItemAndUserNotFound_thenReturnValidationEx() {
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest(1L, "text");
        long itemId = 1L;
        long userId = 1L;

        assertThrows(ValidationEx.class, () -> commentsService.addComments(commentDtoRequest, itemId, userId));
    }

    @Test
    void addComments_whenItemAndUserFoundAndUserHaveBookingNot_returnComment() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item item = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Comment comment = new Comment(
                1L,
                "text",
                item,
                user,
                Instant.now()
        );
        CommentDtoRequest commentDtoRequest = new CommentDtoRequest(1L, "text");
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndIsBefore(eq(1L), eq(1L), eq(Status.APPROVED), any())
        ).thenReturn(Boolean.FALSE);

        assertThrows(ValidationEx.class, () -> commentsService.addComments(commentDtoRequest, itemId, userId));

        verify(commentsRepository, never()).save(comment);
    }
}