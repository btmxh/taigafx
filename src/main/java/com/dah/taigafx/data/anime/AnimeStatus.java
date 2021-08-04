package com.dah.taigafx.data.anime;

import org.jetbrains.annotations.NotNull;

public enum AnimeStatus {
    COMPLETED("Completed"),
    AIRING("Airing"),
    NOT_RELEASED("Not released"),
    CANCELLED("Cancelled"),
    HIATUS("On hiatus"),
    UNKNOWN("Unknown");

    private final @NotNull String displayString;

    AnimeStatus(@NotNull String displayString) {
        this.displayString = displayString;
    }

    @Override
    public @NotNull String toString() {
        return displayString;
    }
}
