//package ru.practicum.shareit.item;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.booking.BookingService;
//import ru.practicum.shareit.item.dto.*;
//import ru.practicum.shareit.request.ItemRequestService;
//import ru.practicum.shareit.user.UserService;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest
//class ItemControllerTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private UserService userService;
//    @MockBean
//    private BookingService bookingService;
//    @MockBean
//    private ItemService itemService;
//    @MockBean
//    private CommentsService commentsService;
//    @MockBean
//    private ItemRequestService itemRequestService;
//
//    @SneakyThrows
//    @Test
//    void getItemByUser() {
//        long userId = 1L;
//        int from = 1;
//        int size = 10;
//        when(itemService.getItemByUserId(userId, from, size)).thenReturn(List.of());
//
//        mockMvc.perform(get("/items?from=1&size=10").header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//
//        verify(itemService).getItemByUserId(userId, from, size);
//
//    }
//
//    @SneakyThrows
//    @Test
//    void addItem() {
//        long userId = 1L;
//        ItemDto itemDto = new ItemDto(
//                1L,
//                "Item1",
//                "desc1",
//                Boolean.TRUE,
//                null
//
//        );
//        when(itemService.addItem(itemDto, userId)).thenReturn(itemDto);
//
//        String result = mockMvc.perform(post("/items").header(Constants.HEADER, userId)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(itemDto)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        assertEquals(objectMapper.writeValueAsString(itemDto), result);
//
//    }
//
//    @SneakyThrows
//    @Test
//    void deleteItem() {
//        long userId = 1L;
//        long itemId = 1L;
//
//        mockMvc.perform(delete("/items/{itemId}", itemId).header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//        verify(itemService).deleteItem(userId, itemId);
//    }
//
//    @SneakyThrows
//    @Test
//    void getItemById() {
//        long itemId = 1L;
//        long userId = 1L;
//        ItemDtoBooking itemDtoBooking = new ItemDtoBooking(
//                1L,
//                "item1",
//                "desc1",
//                Boolean.TRUE,
//                1L,
//                1L,
//                null,
//                null,
//                List.of()
//        );
//        when(itemService.getItem(itemId, userId)).thenReturn(itemDtoBooking);
//        mockMvc.perform(get("/items/{itemId}", itemId).header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//    }
//
//    @SneakyThrows
//    @Test
//    void updateItem() {
//        long itemId = 1L;
//        long userId = 1L;
//        ItemDto itemDto = new ItemDto(
//                1L,
//                "item1",
//                "desc1",
//                Boolean.TRUE,
//                null
//        );
//        when(itemService.updateItem(userId, itemId, itemDto)).thenReturn(itemDto);
//
//        String result = mockMvc.perform(patch("/items/{itemId}", itemId).header(Constants.HEADER, userId)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(itemDto)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        assertEquals(objectMapper.writeValueAsString(itemDto), result);
//    }
//
//    @SneakyThrows
//    @Test
//    void searchItem() {
//        ItemDto itemDto = new ItemDto(
//                1L,
//                "item1",
//                "desc1",
//                Boolean.TRUE,
//                null
//        );
//        List<ItemDto> items = List.of(itemDto);
//        String text = "text";
//        int from = 1;
//        int size = 1;
//        when(itemService.searchItem(text, from, size)).thenReturn(items);
//
//        String result = mockMvc.perform(get("/items/search?text={text}&from={from}&size={size}", text, from, size))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        assertEquals(objectMapper.writeValueAsString(items), result);
//    }
//
//    @SneakyThrows
//    @Test
//    void addComment() {
//        CommentDto commentDto = new CommentDto(
//                1L,
//                "text",
//                1L,
//                "author",
//                "created"
//        );
//        CommentDtoRequest commentDtoRequest = new CommentDtoRequest(
//                1L,
//                "text"
//        );
//        long userId = 1L;
//        long itemId = 1L;
//        when(commentsService.addComments(commentDtoRequest, itemId, userId)).thenReturn(commentDto);
//        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId).header(Constants.HEADER, userId)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(commentDtoRequest)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        assertEquals(objectMapper.writeValueAsString(commentDto), result);
//    }
//}