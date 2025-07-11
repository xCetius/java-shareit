package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    private Long id;

    @NotBlank(message = "name should not be empty")
    @NotNull(message = "name should not be null")
    private String name;

    @NotNull(message = "email should not be null")
    @NotBlank(message = "email should not be empty")
    @Email(message = "email should be valid")
    private String email;
}
