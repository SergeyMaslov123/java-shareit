package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.Marker;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Validated
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;

    @Override
    @Transactional
    @Validated({Marker.OnCreate.class})
    public ItemDto addItem(@Valid ItemDto itemDto, long userId) {
        Item item = ItemMapper.toDtoItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDtoBooking> getItemByUserId(long owner) {
        return itemRepository.findByOwnerId(owner).stream()
                .map(item -> getLastAndNextBooking(item, owner))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (oldItem.getOwner().getId() == userId) {
            Item newItem = ItemMapper.toDtoItem(itemDto);
            if (newItem.getName() != null && !newItem.getName().isBlank()) {
                oldItem.setName(newItem.getName());
            }
            if (newItem.getDescription() != null && !newItem.getDescription().isBlank()) {
                oldItem.setDescription(newItem.getDescription());
            }
            if (newItem.getAvailable() != null) {
                oldItem.setAvailable(newItem.getAvailable());
            }
            return ItemMapper.toItemDto(itemRepository.save(oldItem));
        } else {
            throw new EntityNotFoundException("User not item");
        }
    }

    @Override
    public ItemDtoBooking getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("item not"));
        return getLastAndNextBooking(item, userId);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            String textToLower = text.toLowerCase();
            return itemRepository.searchItem(textToLower).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    private ItemDtoBooking getLastAndNextBooking(Item item, long userId) {
        List<CommentDto> comments = commentsRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        List<Booking> allLastBookings = bookingRepository.findByItem_IdAndStatusAndStartIsBeforeOrderByStartDesc(
                item.getId(),
                Status.APPROVED,
                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());

        BookingDto lastBooking = null;
        if (!allLastBookings.isEmpty() && item.getOwner().getId().equals(userId)) {
            lastBooking = allLastBookings
                    .stream()
                    .findFirst()
                    .map(BookingMapper::toBookingDto)
                    .get();
        }

        List<Booking> allNextBooking = bookingRepository.findByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(
                item.getId(),
                Status.APPROVED,
                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());
        BookingDto nextBooking = null;
        if (!allNextBooking.isEmpty() && item.getOwner().getId().equals(userId)) {
            nextBooking = allNextBooking
                    .stream()
                    .findFirst()
                    .map(BookingMapper::toBookingDto)
                    .get();
        }
        return ItemMapper.toItemDtoBooking(item, lastBooking,
                nextBooking, comments);
    }
}
