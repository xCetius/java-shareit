package ru.practicum.shareit.enums;

import ru.practicum.shareit.exception.ConfirmationException;

import java.util.Arrays;

public enum BookingStatus {

    WAITING("waiting"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELED("canceled");

    private final String paramValue;

    BookingStatus(String paramValue) {
        this.paramValue = paramValue;
    }

    public static BookingStatus fromString(String value) {
        return Arrays.stream(BookingStatus.values())
                .filter(e -> e.paramValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ConfirmationException(
                        "Unknown search criteria: " + value
                ));
    }
}
