package com.dah.taigafx.animelist;

import org.jetbrains.annotations.NotNull;

public enum AnimeListService {
    USE_NONE("None"),
    MYANIMELIST("MyAnimeList (myanimelist.net)"),
    ANILIST("AniList (anilist.co)"),
    KITSU("Kitsu (kitsu.io)");

    private @NotNull final String displayString;

    AnimeListService(@NotNull String displayString) {
        this.displayString = displayString;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
