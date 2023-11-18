package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.Marker;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
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
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    @Validated({Marker.OnCreate.class})
    public ItemDto addItem(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toDtoItem(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository
                    .findById(itemDto.getRequestId()).orElseThrow(() -> new EntityNotFoundException("itemRequest not found"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void deleteItem(long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDtoBooking> getItemByUserId(long owner, Integer from, Integer size) {
        Map<Long, Item> itemsForUser;
        if (from != null && size != null) {
            Pageable pageable = PageRequest.of(from, size);
            itemsForUser = itemRepository.findAllByOwnerId(owner, pageable)
                    .stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
        } else {
            itemsForUser = itemRepository.findByOwnerId(owner)
                    .stream()
                    .collect(Collectors.toMap(Item::getId, Function.identity()));
        }
        Map<Item, List<Comment>> commentsMap = commentsRepository.findAllByItemIdInOrderByCreatedAsc(itemsForUser.keySet())
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem));
        Map<Item, List<Booking>> allLastBokingsMap =
                bookingRepository.findAllByItem_IdInAndStatusAndStartLessThanEqualOrderByStartDesc(
                                itemsForUser.keySet(),
                                Status.APPROVED,
                                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant())
                        .stream()
                        .collect(Collectors.groupingBy(Booking::getItem));
        Map<Item, List<Booking>> allNextBooking =
                bookingRepository.findAllByItem_IdInAndStatusAndStartIsAfterOrderByStartAsc(
                                itemsForUser.keySet(),
                                Status.APPROVED,
                                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
                        ).stream()
                        .collect(Collectors.groupingBy(Booking::getItem));

        List<ItemDtoBooking> allItem = new LinkedList<>();
        for (Item item : itemsForUser.values()) {
            List<CommentDto> allComment = new LinkedList<>();
            BookingDto lastBooking = null;
            BookingDto nextBooking = null;
            if (!allLastBokingsMap.isEmpty() && allLastBokingsMap.containsKey(item)) {
                lastBooking = BookingMapper.toBookingDto(allLastBokingsMap.get(item).get(0));
            }
            if (!allNextBooking.isEmpty() && allNextBooking.containsKey(item)) {
                nextBooking = BookingMapper.toBookingDto(allNextBooking.get(item).get(0));
            }
            if (!commentsMap.isEmpty() && commentsMap.containsKey(item)) {
                allComment = commentsMap.get(item).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
            }
            allItem.add(ItemMapper.toItemDtoBooking(
                    item,
                    lastBooking,
                    nextBooking,
                    allComment));
        }
        return allItem;
    }

    @Override
    @Transactional
    @Validated(Marker.OnUpdate.class)
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
    public List<ItemDto> searchItem(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        } else {
            if (from != null && size != null) {
                Pageable pageable = PageRequest.of(from, size);
                return itemRepository.searchItemPage(text, pageable)
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
            } else {
                return itemRepository.searchItem(text).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
            }
        }
    }

    private ItemDtoBooking getLastAndNextBooking(Item item, long userId) {
        List<CommentDto> comments = commentsRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        BookingDto lastBookingDto = null;
        Booking lastBooking = bookingRepository.findFirstByItem_IdAndStatusAndStartLessThanEqualOrderByStartDesc(
                item.getId(),
                Status.APPROVED,
                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant()
        );
        if (lastBooking != null && item.getOwner().getId().equals(userId)) {
            lastBookingDto = BookingMapper.toBookingDto(lastBooking);
        }
        Booking nextBooking = bookingRepository.findFirstByItem_IdAndStatusAndStartIsAfterOrderByStartAsc(
                item.getId(),
                Status.APPROVED,
                LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant());
        BookingDto nextBookingDto = null;
        if (nextBooking != null && item.getOwner().getId().equals(userId)) {
            nextBookingDto = BookingMapper.toBookingDto(nextBooking);
        }
        return ItemMapper.toItemDtoBooking(item, lastBookingDto,
                nextBookingDto, comments);
    }
}
