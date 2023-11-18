package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.Generated;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
@Generated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item.getRequest() != null) {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest().getId()
            );
        } else {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null);
        }
    }

    public static Item toDtoItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                null,
                null
        );
    }

    public static ItemDtoBooking toItemDtoBooking(Item item,
                                                  BookingDto lastBooking,
                                                  BookingDto nextBooking,
                                                  List<CommentDto> comments) {
        return new ItemDtoBooking(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                null,
                lastBooking,
                nextBooking,
                comments);
    }

    public static Item toDtoItem(ItemDtoBooking itemDtoBooking) {
        return new Item(
                itemDtoBooking.getId(),
                itemDtoBooking.getName(),
                itemDtoBooking.getDescription(),
                itemDtoBooking.getAvailable(),
                null,
                null
        );
    }

    public static ItemDtoForBookingRequest toItemDtoForBR(Item item) {
        return new ItemDtoForBookingRequest(item.getId(),
                item.getName());
    }
}
