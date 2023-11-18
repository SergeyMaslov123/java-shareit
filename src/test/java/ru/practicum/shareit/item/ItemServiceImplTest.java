package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;
    @Captor
    private ArgumentCaptor<Item> argumentCaptor;


    @Test
    void addItem_whenUserFound_thenReturnItemDto() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                null
        );
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item item = ItemMapper.toDtoItem(itemDto);
        item.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto actualItemDto = itemService.addItem(itemDto, userId);

        assertEquals(actualItemDto, itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void addItem_whenUserFoundRequestFound_thenReturnItemDto() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                1L
        );
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        User user2 = new User(
                2L,
                "Rob1",
                "stark1@mail.ru"
        );
        ItemRequest itemRequest = new ItemRequest(1L, "desc", user2, Instant.now());
        Item item = ItemMapper.toDtoItem(itemDto);
        item.setOwner(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));

        ItemDto actualItemDto = itemService.addItem(itemDto, userId);

        assertEquals(actualItemDto, itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void addItem_whenUserFoundRequestNotFound_thenReturnItemDto() {
        long userId = 1L;
        ItemDto itemDto = new ItemDto(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                1L
        );
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        User user2 = new User(
                2L,
                "Rob1",
                "stark1@mail.ru"
        );
        ItemRequest itemRequest = new ItemRequest(1L, "desc", user2, Instant.now());
        Item item = ItemMapper.toDtoItem(itemDto);
        item.setOwner(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(itemDto, userId));

        verify(itemRepository, never()).save(item);
    }

    @Test
    void deleteItem() {
        long userId = 1L;
        long itemId = 1L;
        itemService.deleteItem(userId, itemId);

        verify(itemRepository).deleteById(itemId);
    }

    @Test
    void getItemByUserId() {
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
        Item item2 = new Item(
                2L,
                "item2",
                "desc2",
                Boolean.TRUE,
                user,
                null
        );
        List<Item> items = List.of(item, item2);
        Page<Item> itemsPage = new PageImpl<>(items);
        ItemDtoBooking itemDtoBooking1 = ItemMapper.toItemDtoBooking(item, null, null, List.of());
        ItemDtoBooking itemDtoBooking2 = ItemMapper.toItemDtoBooking(item2, null, null, List.of());
        List<ItemDtoBooking> listItems = List.of(itemDtoBooking1, itemDtoBooking2);
        Pageable pageable = PageRequest.of(5, 3);
        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(itemsPage);

        List<ItemDtoBooking> actualListItemDto = itemService.getItemByUserId(userId, 5, 3);

        assertEquals(listItems.size(), actualListItemDto.size());
        assertEquals(listItems, actualListItemDto);
    }

    @Test
    void getItemByUserId_whenFromSizeNot_thenReturnItems() {
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
        Item item2 = new Item(
                2L,
                "item2",
                "desc2",
                Boolean.TRUE,
                user,
                null
        );

        ItemDtoBooking itemDtoBooking1 = ItemMapper.toItemDtoBooking(item, null, null, List.of());
        ItemDtoBooking itemDtoBooking2 = ItemMapper.toItemDtoBooking(item2, null, null, List.of());
        List<ItemDtoBooking> listItems = List.of(itemDtoBooking1, itemDtoBooking2);
        when(itemRepository.findByOwnerId(userId)).thenReturn(List.of(item, item2));

        List<ItemDtoBooking> actualListItemDto = itemService.getItemByUserId(userId, null, null);

        assertEquals(listItems.size(), actualListItemDto.size());
        assertEquals(listItems, actualListItemDto);
    }

    @Test
    void updateItem_whenItemFound_thenReturnItemDto() {
        long userId = 1L;
        long itemId = 1L;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item oldItem = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Item newItem = new Item(
                1L,
                "item2",
                "desc2",
                Boolean.TRUE,
                user,
                null
        );
        ItemDto newItemDto = ItemMapper.toItemDto(newItem);
        when(itemRepository.findById(userId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(oldItem)).thenReturn(newItem);

        ItemDto actualItemDto = itemService.updateItem(userId, itemId, newItemDto);

        verify(itemRepository).save(oldItem);
        verify(itemRepository).save(argumentCaptor.capture());
        Item itemSave = argumentCaptor.getValue();

        assertEquals(newItemDto, actualItemDto);
        assertEquals(itemSave, newItem);
    }

    @Test
    void updateItem_whenItemNotFound_thenThrowNotFoundEx() {
        long userId = 1L;
        long itemId = 1L;
        Item oldItem = new Item();
        ItemDto itemDto = new ItemDto(1L, "name", "desc", Boolean.TRUE, null);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(userId, itemId, itemDto));
        verify(itemRepository, never()).save(oldItem);
    }

    @Test
    void updateItem_whenItemFoundNotUserOwner_thenThrowEntityNotFoundEx() {
        long userId = 2L;
        long itemId = 1L;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item oldItem = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Item newItem = new Item(
                1L,
                "item2",
                "desc2",
                Boolean.TRUE,
                user,
                null
        );
        ItemDto newItemDto = ItemMapper.toItemDto(newItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(userId, itemId, newItemDto));
        verify(itemRepository, never()).save(oldItem);
    }

    @Test
    void updateItem_whenNameNull_thenReturnItemDtoWithOldName() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item oldItem = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Item newItem = new Item(
                1L,
                null,
                "desc2",
                Boolean.TRUE,
                user,
                null
        );
        ItemDto newItemDto = ItemMapper.toItemDto(newItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(oldItem)).thenReturn(oldItem);

        itemService.updateItem(userId, itemId, newItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item actualItem = argumentCaptor.getValue();

        assertEquals("item1", actualItem.getName());
        assertEquals("desc2", actualItem.getDescription());
        assertTrue(actualItem.getAvailable());
        assertNull(actualItem.getRequest());

    }

    @Test
    void updateItem_whenDescriptionNull_thenReturnItemDtoWithOldName() {
        long itemId = 1L;
        long userId = 1L;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item oldItem = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Item newItem = new Item(
                1L,
                "item2",
                null,
                Boolean.TRUE,
                user,
                null
        );
        ItemDto newItemDto = ItemMapper.toItemDto(newItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(oldItem)).thenReturn(oldItem);

        itemService.updateItem(userId, itemId, newItemDto);

        verify(itemRepository).save(argumentCaptor.capture());
        Item actualItem = argumentCaptor.getValue();

        assertEquals("item2", actualItem.getName());
        assertEquals("desc1", actualItem.getDescription());
        assertTrue(actualItem.getAvailable());
        assertNull(actualItem.getRequest());

    }


    @Test
    void getItem_whenItemFound_thenReturnItem() {
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
        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoBooking(item, null, null, List.of());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(commentsRepository.findByItemId(itemId)).thenReturn(List.of());

        ItemDtoBooking actualItemDto = itemService.getItem(itemId, userId);

        assertEquals(itemDtoBooking, actualItemDto);
    }

    @Test
    void getItem_whenItemNotFound_returnEntityNotFoundEx() {
        long itemId = 1L;
        long userId = 1L;
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(itemId, userId));
    }

    @Test
    void searchItem_whenTextNotBlanckItemFound_thenReturnListItem() {
        String text = "test text";
        int from = 1;
        int size = 10;
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item item1 = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Item item2 = new Item(
                1L,
                "item2",
                null,
                Boolean.TRUE,
                user,
                null
        );
        List<Item> items = List.of(item1, item2);
        Pageable pageable = PageRequest.of(from, size);
        Page<Item> itemsPage = new PageImpl<>(items);
        when(itemRepository.searchItemPage(text, pageable)).thenReturn(itemsPage);
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        List<ItemDto> actualListItems = itemService.searchItem(text, from, size);

        assertEquals(itemsDto, actualListItems);
        assertEquals(itemsDto.size(), actualListItems.size());
    }

    @Test
    void searchItem_whenTextIsBlank_thenReturnListEmpty() {
        String text = " ";
        int from = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(from, size);


        List<ItemDto> actualListItems = itemService.searchItem(text, from, size);

        assertEquals(0, actualListItems.size());
        verify(itemRepository, never()).searchItemPage(text, pageable);
    }

    @Test
    void searchItem_whenTextNotBlanckItemFoundFromSizeNull_thenReturnListItem() {
        String text = "test text";
        User user = new User(
                1L,
                "Rob",
                "stark@mail.ru"
        );
        Item item1 = new Item(
                1L,
                "item1",
                "desc1",
                Boolean.TRUE,
                user,
                null
        );
        Item item2 = new Item(
                1L,
                "item2",
                null,
                Boolean.TRUE,
                user,
                null
        );
        List<Item> items = List.of(item1, item2);
        when(itemRepository.searchItem(text)).thenReturn(items);
        List<ItemDto> itemsDto = items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        List<ItemDto> actualListItems = itemService.searchItem(text, null, null);

        assertEquals(itemsDto, actualListItems);
        assertEquals(itemsDto.size(), actualListItems.size());
    }

}