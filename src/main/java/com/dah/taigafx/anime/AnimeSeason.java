package com.dah.taigafx.anime;

import org.jetbrains.annotations.NotNull;

public enum AnimeSeason {
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter"),
    UNKNOWN("Unknown");

    private @NotNull final String displayName;

    AnimeSeason(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public static record WithYear(AnimeSeason season, int year) {
        @Override
        public String toString() {
            return season.displayName + " " + year;
        }
    }
}
