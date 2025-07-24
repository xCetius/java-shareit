package ru.practicum.shareit.enums;

import ru.practicum.shareit.exception.ConfirmationException;

import java.util.Arrays;

public enum ConfirmationStatus {

    WAITING("waiting"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELED("canceled");

    private final String paramValue;

    ConfirmationStatus(String paramValue) {
        this.paramValue = paramValue;
    }

    public static ConfirmationStatus fromString(String value) {
        return Arrays.stream(ConfirmationStatus.values())
                .filter(e -> e.paramValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new ConfirmationException(
                        "Unknown search criteria: " + value
                ));
    }
}
