package ru.practicum.shareit.enums;

import ru.practicum.shareit.exception.ConfirmationException;

import java.util.Arrays;

public enum Confirmation {

    WAITING("waiting"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELED("canceled");

    private final String paramValue;

    Confirmation(String paramValue) {
        this.paramValue = paramValue;
    }

    public static Confirmation fromString(String value) {
        return Arrays.stream(Confirmation.values())
                .filter(e -> e.paramValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ConfirmationException(
                        "Unknown search criteria: " + value
                ));
    }
}
