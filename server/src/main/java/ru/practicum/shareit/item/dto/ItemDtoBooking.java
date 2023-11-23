package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Value
@Setter
@Getter
public class ItemDtoBooking {
    long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long request;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;

}
