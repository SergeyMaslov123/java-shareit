//package ru.practicum.shareit.booking;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.booking.dto.BookingDtoAnswer;
//import ru.practicum.shareit.booking.dto.BookingDtoRequest;
//import ru.practicum.shareit.item.CommentsService;
//import ru.practicum.shareit.item.Constants;
//import ru.practicum.shareit.item.ItemService;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.request.ItemRequestService;
//import ru.practicum.shareit.user.User;
//import ru.practicum.shareit.user.UserService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest
//class BookingControllerTest {
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
//    long userId = 1L;
//    long bookingId = 1L;
//    String state = "ALL";
//
//    BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(
//            1L,
//            LocalDateTime.now().plusHours(10),
//            LocalDateTime.now().plusHours(20),
//            1L
//    );
//    User user = new User(
//            1L,
//            "Rob",
//            "stark@mail.ru"
//    );
//
//    Item item = new Item(
//            1L,
//            "item1",
//            "desc1",
//            Boolean.TRUE,
//            user,
//            null
//    );
//    Boolean approved = true;
//    BookingDtoAnswer bookingDtoAnswer = new BookingDtoAnswer(
//            1L,
//            LocalDateTime.now().plusHours(10),
//            LocalDateTime.now().plusHours(20),
//            null,
//            null,
//            Status.WAITING
//    );
//
//
//    @SneakyThrows
//    @Test
//    void addBooking() {
//        Booking booking = BookingMapper.toBookingFromRequest(bookingDtoRequest);
//        booking.setBooker(user);
//        booking.setItem(item);
//        BookingDtoAnswer bookingDtoAnswer = BookingMapper.toBookingDtoAnswer(booking);
//        when(bookingService.addBooking(userId, bookingDtoRequest)).thenReturn(bookingDtoAnswer);
//        String result = mockMvc.perform(post("/bookings")
//                        .header(Constants.HEADER, userId)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(bookingDtoRequest)))
//                .andExpect(status().isOk())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        assertEquals(objectMapper.writeValueAsString(bookingDtoAnswer), result);
//    }
//
//    @SneakyThrows
//    @Test
//    void approvedBooking() {
//        when(bookingService.approvedBooking(userId, bookingId, approved)).thenReturn(bookingDtoAnswer);
//
//        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", bookingId, approved)
//                        .header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//    }
//
//    @SneakyThrows
//    @Test
//    void getBooking() {
//        when(bookingService.getBooking(userId, bookingId)).thenReturn(bookingDtoAnswer);
//
//        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
//                        .header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//    }
//
//    @SneakyThrows
//    @Test
//    void getAllBookingForUserId() {
//        List<BookingDtoAnswer> bookings = List.of(bookingDtoAnswer);
//        when(bookingService.getAllBookingsForUserId(userId, state, 1, 10)).thenReturn(bookings);
//
//        mockMvc.perform(get("/bookings?state={state}&from={from}&size={size}", state, 1, 10)
//                        .header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//    }
//
//    @SneakyThrows
//    @Test
//    void getAllBookingsForUserItems() {
//        List<BookingDtoAnswer> bookings = List.of(bookingDtoAnswer);
//        when(bookingService.getAllBookingForUserOwner(userId, state, 1, 10)).thenReturn(bookings);
//
//        mockMvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", state, 1, 10)
//                        .header(Constants.HEADER, userId))
//                .andExpect(status().isOk());
//    }
//}