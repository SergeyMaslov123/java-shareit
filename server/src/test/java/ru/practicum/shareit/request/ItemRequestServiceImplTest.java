package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    ItemRequestDto itemRequestDto = new ItemRequestDto(1, "text");
    Long userId = 1L;
    ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
    User user = new User(1L, "name", "email@mail.ru");
    Item item = new Item(1L, "item", "description", Boolean.TRUE, user, itemRequest);

    @Test
    void addItemRequest_whenUserFound_thenReturnItemRequest() {

        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequestDtoAnswerThenCreate itemRequestDto1 = ItemRequestMapper.toItemDtoAnswerThenCreate(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDtoAnswerThenCreate actualItemRequest = itemRequestService.addItemRequest(itemRequestDto, userId);

        assertEquals(itemRequestDto1.getId(), actualItemRequest.getId());
    }

    @Test
    void addItemRequest_whenUserNotFound_thenThrowEntityNotFoundEx() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.addItemRequest(itemRequestDto, userId));

        verify(itemRequestRepository, never()).save(itemRequest);
    }


    @Test
    void getItemRequestForUser_whenItemRequestFoundItemFound_thenReturnListItemRequest() {
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        List<Item> items = List.of(item);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreated(1L)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(items);
        List<ItemDto> allItemDto = items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequestDtoAnswer itemRequestDtoAnswer = ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, allItemDto);
        List<ItemRequestDtoAnswer> itemRequestDtoAnswerList = List.of(itemRequestDtoAnswer);

        List<ItemRequestDtoAnswer> actualListItemRequest = itemRequestService.getItemRequestForUser(1L);

        assertEquals(itemRequestDtoAnswerList, actualListItemRequest);
        assertEquals(itemRequestDtoAnswerList.size(), actualListItemRequest.size());
    }

    @Test
    void getItemRequestForUser_whenItemRequestFoundItemNotFound_thenReturnListItemRequest() {
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreated(1L)).thenReturn(itemRequests);
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());
        ItemRequestDtoAnswer itemRequestDtoAnswer = ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, List.of());
        List<ItemRequestDtoAnswer> itemRequestDtoAnswerList = List.of(itemRequestDtoAnswer);

        List<ItemRequestDtoAnswer> actualListItemRequest = itemRequestService.getItemRequestForUser(1L);

        assertEquals(itemRequestDtoAnswerList, actualListItemRequest);
        assertEquals(itemRequestDtoAnswerList.size(), actualListItemRequest.size());
    }

    @Test
    void getItemRequestForUser_whenItemRequestNotFound_thenReturnListItemRequest() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreated(1L)).thenReturn(List.of());
        List<ItemRequestDtoAnswer> itemRequestDtoAnswerList = List.of();

        List<ItemRequestDtoAnswer> actualListItemRequest = itemRequestService.getItemRequestForUser(1L);

        assertEquals(itemRequestDtoAnswerList, actualListItemRequest);
        assertEquals(actualListItemRequest.size(), 0);
    }

    @Test
    void deleteItemRequest() {
    }

    @Test
    void getAllRequests_whenItemRequestFound_thenReturnItemRequestList() {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page = PageRequest.of(1, 10, sort);
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests);
        List<ItemRequestDtoAnswer> listRequests = itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, List.of(ItemMapper.toItemDto(item)))).collect(Collectors.toList());

        when(itemRequestRepository.findAll(page)).thenReturn(itemRequestPage);
        when(itemRepository.findAllByRequestIdIn(Set.of(1L))).thenReturn(List.of(item));

        List<ItemRequestDtoAnswer> actualListItemRequest = itemRequestService.getAllRequests(2L, 1, 10);

        assertEquals(listRequests, actualListItemRequest);
    }

    @Test
    void getItemRequestForId_whenUserItemRequestItemFound_thenReturnItemRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(1L)).thenReturn(List.of(item));
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        ItemRequestDtoAnswer itemRequestDtoAnswer = ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, List.of(ItemMapper.toItemDto(item)));
        ItemRequestDtoAnswer actualItemRequest = itemRequestService.getItemRequestForId(2L, 1L);
        assertEquals(itemRequestDtoAnswer, actualItemRequest);
    }

    @Test
    void getItemRequestForId_whenUserItemItemRequestNotFound_thenThrowEntityNotFoundEx() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getItemRequestForId(1L, 1L));
    }
}