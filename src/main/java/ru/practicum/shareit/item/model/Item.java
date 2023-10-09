package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer id;
    private ItemRequest request;
}
