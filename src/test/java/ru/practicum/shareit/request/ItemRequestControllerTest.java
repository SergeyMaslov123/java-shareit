package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.CommentsService;
import ru.practicum.shareit.item.Constants;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoAnswerThenCreate;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentsService commentsService;
    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void addRequest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description");
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(Instant.now());
        itemRequest.setId(1L);
        itemRequest.setRequestor(new User());
        ItemRequestDtoAnswerThenCreate itemRequestDtoAnswerThenCreate = ItemRequestMapper.toItemDtoAnswerThenCreate(itemRequest);
        when(itemRequestService.addItemRequest(itemRequestDto, 1L)).thenReturn(itemRequestDtoAnswerThenCreate);

        String actualRequest = mockMvc.perform(post("/requests").header(Constants.HEADER, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoAnswerThenCreate), actualRequest);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestForUser() {
        long userId = 1L;
        mockMvc.perform(get("/requests").header(Constants.HEADER, userId))
                .andExpect(status().isOk());
        verify(itemRequestService).getItemRequestForUser(userId);
    }

    @SneakyThrows
    @Test
    void getAllItemRequest() {
        long userId = 1L;
        int from = 1;
        int size = 10;

        mockMvc.perform(get("/requests/all?from={from}&size={size}", from, size)
                        .header(Constants.HEADER, userId))
                .andExpect(status().isOk());
        verify(itemRequestService).getAllRequests(userId, from, size);
    }

    @SneakyThrows
    @Test
    void getItemRequestById() {
        long userId = 1L;
        long requestId = 1L;

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Constants.HEADER, userId))
                .andExpect(status().isOk());
    }
}