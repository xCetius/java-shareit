package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {

    private Long id;


    @NotNull
    private Item item;

}
