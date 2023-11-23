package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ConstatntsGateway;
import ru.practicum.shareit.request.dto.RequestRequestDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(ConstatntsGateway.HEADER) Long userId,
                                             @RequestBody RequestRequestDto itemRequestDto) {
        return requestClient.addRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestForUser(@RequestHeader(ConstatntsGateway.HEADER) Long userId) {
        return requestClient.getAllItemRequestForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader(ConstatntsGateway.HEADER) Long userId,
                                                    @RequestParam(required = false) Integer from,
                                                    @RequestParam(required = false) Integer size) {
        return requestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(ConstatntsGateway.HEADER) Long userId,
                                                     @PathVariable Long requestId) {
        return requestClient.getItemRequestById(userId, requestId);
    }
}
