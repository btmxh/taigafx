package com.dah.taigafx.data.anime;

import org.jetbrains.annotations.NotNull;

public enum AnimeSeason {
    WINTER("Winter"),
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall"),
    UNKNOWN("Unknown");

    private @NotNull final String displayName;

    AnimeSeason(@NotNull String displayName) {
        this.displayName = displayName;
    }

    public static record WithYear(AnimeSeason season, int year) implements Comparable<WithYear> {
        @Override
        public String toString() {
            if(season == UNKNOWN) {
                return year == 0? "Unknown" : ("Unknown (" + year + ")");
            } else {
                return season.displayName + " " + year;
            }
        }

        @Override
        public int compareTo(@NotNull AnimeSeason.WithYear o) {
            if(season == UNKNOWN && o.season == UNKNOWN) {
                return year - o.year;
            } else if(season == UNKNOWN) {
                return -1;
            } else if(o.season == UNKNOWN) {
                return 1;
            }

            return year == o.year? season.compareTo(o.season) : (year - o.year);
        }
    }
}
