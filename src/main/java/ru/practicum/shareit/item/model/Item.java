package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private Long id;

    @NotBlank(message = "name should not be empty")
    private String name;

    @NotBlank(message = "description should not be empty")
    private String description;

    @NotNull(message = "available should not be empty")
    private Boolean available;

    private User owner;

    private ItemRequest request;


}
