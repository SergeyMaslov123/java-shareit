package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.Valid;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Validated
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDtoAnswerThenCreate addItemRequest(@Valid ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(Instant.now());
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        itemRequest.setRequestor(user);
        ItemRequest itemRequest1 = itemRequestRepository.save(itemRequest);
        itemRequest1.setCreated(ZonedDateTime.now().toInstant());
        return ItemRequestMapper.toItemDtoAnswerThenCreate(itemRequest1);
    }

    @Override
    public List<ItemRequestDtoAnswer> getItemRequestForUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreated(id);
        List<ItemRequestDtoAnswer> allRequestDto = new LinkedList<>();
        if (!itemRequests.isEmpty()) {
            Map<Long, ItemRequest> allRequests = itemRequests.stream()
                    .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
            List<Item> itemsByRequest = itemRepository.findAllByRequestIdIn(allRequests.keySet());
            Map<ItemRequest, List<Item>> allItemForRequest = new HashMap<>();
            if (!itemsByRequest.isEmpty()) {
                allItemForRequest = itemsByRequest.stream()
                        .collect(Collectors.groupingBy(Item::getRequest));
            }
            for (ItemRequest itemRequest : allRequests.values()) {
                List<ItemDto> allItemDto = new LinkedList<>();
                if (!allItemForRequest.isEmpty() && allItemForRequest.containsKey(itemRequest)) {
                    allItemDto = allItemForRequest.get(itemRequest)
                            .stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                }
                ItemRequestDtoAnswer itemRequestDtoAnswer = ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, allItemDto);
                allRequestDto.add(itemRequestDtoAnswer);
            }
        }
        return allRequestDto;
    }

    @Override
    public List<ItemRequestDtoAnswer> getAllRequests(Long userId, Integer from, Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        Pageable page;
        List<ItemRequestDtoAnswer> allRequestDto = new ArrayList<>();
        if (from != null && size != null) {
            page = PageRequest.of(from, size, sort);
            Page<ItemRequest> itemRequestPage = itemRequestRepository.findAll(page);
            if (!itemRequestPage.isEmpty()) {
                Map<Long, ItemRequest> itemRequestMap = itemRequestPage.stream()
                        .filter(itemRequest -> !itemRequest.getRequestor().getId().equals(userId))
                        .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
                List<Item> items = itemRepository.findAllByRequestIdIn(itemRequestMap.keySet());
                if (!items.isEmpty()) {
                    Map<ItemRequest, List<Item>> itemsForRequest = items.stream()
                            .collect(Collectors.groupingBy(Item::getRequest));
                    for (ItemRequest itemRequest : itemRequestMap.values()) {
                        List<ItemDto> allItemDto = List.of();
                        if (!itemsForRequest.isEmpty() && itemsForRequest.containsKey(itemRequest)) {
                            allItemDto = itemsForRequest.get(itemRequest)
                                    .stream()
                                    .map(ItemMapper::toItemDto)
                                    .collect(Collectors.toList());
                        }
                        ItemRequestDtoAnswer itemRequestDtoAnswer =
                                ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, allItemDto);
                        allRequestDto.add(itemRequestDtoAnswer);
                    }
                }
            }
            return allRequestDto;
        } else {
            return List.of();
        }
    }

    @Override
    public ItemRequestDtoAnswer getItemRequestForId(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("itemRequest not found"));
        List<ItemDto> items = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDtoAnswer(itemRequest, items);
    }
}
