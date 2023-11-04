package ru.practicum.shareit.request;

public interface ItemRequestService {
    ItemRequest addItemRequest(ItemRequest itemRequest);

    ItemRequest getItemRequest(Long id);

    void deleteItemRequest(Long id);
}
