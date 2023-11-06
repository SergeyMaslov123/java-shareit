package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@AllArgsConstructor
public class CommentsServiceImpl implements CommentsService {
    private final CommentsRepository commentsRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto addComments(CommentDto commentDto, Long itemId, Long userId) {
        if (commentDto.getText() == null || commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            throw new ValidationEx("comment text is empty");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidationEx("item not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ValidationEx("User not found"));
        if (!bookingRepository.findByBooker_IdAndItem_IdAndStatusAndEndIsBeforeOrderByStartDesc(
                        userId,
                        itemId,
                        Status.APPROVED,
                        LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())
                .isEmpty()) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());
            return CommentMapper.toCommentDto(commentsRepository.save(comment));
        } else {
            throw new ValidationEx("user not booking ");
        }
    }
}
