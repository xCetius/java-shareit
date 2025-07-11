package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    Long id;

    @NotNull(message = "User name should not be null")
    @NotBlank(message = "User name should not be empty")
    private String name;
    @Email(message = "User email should be valid")
    private String email;

}
