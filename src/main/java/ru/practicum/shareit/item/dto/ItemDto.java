package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;


@Data
@AllArgsConstructor
public class ItemDto {

    private Long id;

    @NotBlank(message = "name should not be empty")
    private String name;

    private String description;

    @NotNull(message = "available should not be null")
    private Boolean available;

    private Long requestId;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;


}
