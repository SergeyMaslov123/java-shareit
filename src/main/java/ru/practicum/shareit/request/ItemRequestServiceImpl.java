package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest) {
        return null;
    }

    @Override
    public ItemRequest getItemRequest(Long id) {
        return null;
    }

    @Override
    public void deleteItemRequest(Long id) {

    }
}
