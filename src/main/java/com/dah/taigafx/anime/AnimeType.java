package com.dah.taigafx.anime;

import org.jetbrains.annotations.NotNull;

public enum AnimeType {
    TV("TV"),
    MOVIE("Movie"),
    SPECIAL("Special"),
    ONA("ONA"),
    OVA("OVA"),
    MUSIC("Music"),
    UNKNOWN("Unknown");

    private final @NotNull String displayString;

    AnimeType(@NotNull String displayString) {
        this.displayString = displayString;
    }

    @Override
    public @NotNull String toString() {
        return displayString;
    }
}
