package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationEx;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;

    @Override
    @Transactional
    public ItemDto addItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toDtoItem(itemDto);
        validateName(item);
        validateDescription(item);
        validateAvailable(item);
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
            if (newItem.getName() != null) {
                validateName(newItem);
                oldItem.setName(newItem.getName());
            }
            if (newItem.getDescription() != null) {
                validateDescription(newItem);
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
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        } else {
            String textToLower = text.toLowerCase();
            return itemRepository.searchItem(textToLower).stream()
                    .map(ItemMapper::toItemDto)
                    .filter(ItemDto::getAvailable)
                    .collect(Collectors.toList());
        }
    }

    private void validateName(Item item) {
        if (item.getName() == null || item.getName().isEmpty() || item.getName().isBlank()) {
            throw new ValidationEx("недопустимые параметры name");
        }
    }

    private void validateAvailable(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationEx("недопустимые параметры available");
        }
    }

    private void validateDescription(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank() || item.getDescription().isEmpty()) {
            throw new ValidationEx("недопустимые параметры Description");
        }
    }

    private ItemDtoBooking getLastAndNextBooking(Item item, long userId) {
        List<Booking> allBookingsForItem = bookingRepository.findByItemId(item.getId()).stream()
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .collect(Collectors.toList());
        List<CommentDto> comments = commentsRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (!allBookingsForItem.isEmpty() && item.getOwner().getId().equals(userId)) {
            List<Booking> allLastBooking = new ArrayList<>();
            List<Booking> allNextBooking = new ArrayList<>();
            allBookingsForItem.forEach(booking -> {
                if (booking.getStart().isBefore(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())) {
                    allLastBooking.add(booking);
                }
                if (booking.getStart().isAfter(LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())) {
                    allNextBooking.add(booking);
                }
            });
            BookingDto lastBooking = null;
            if (!allLastBooking.isEmpty()) {
                lastBooking = BookingMapper.toBookingDto(allLastBooking.stream()
                        .min((b2, b1) -> b1.getStart().compareTo(b2.getStart()))
                        .get());
            }
            BookingDto nextBooking = null;
            if (!allNextBooking.isEmpty()) {
                nextBooking = BookingMapper.toBookingDto(allNextBooking.stream()
                        .max((b2, b1) -> b1.getStart().compareTo(b2.getStart()))
                        .get());
            }
            return ItemMapper.toItemDtoBooking(item, lastBooking,
                    nextBooking, comments);
        } else {
            return ItemMapper.toItemDtoBooking(item, null, null, comments);
        }
    }
}
