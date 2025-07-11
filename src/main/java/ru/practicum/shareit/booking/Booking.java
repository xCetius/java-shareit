package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.enums.Confirmation;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    private Long id;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFrom;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTo;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    @NotNull
    private Confirmation confirmation;


}
