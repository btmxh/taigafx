package com.dah.taigafx.anime;

import org.jetbrains.annotations.NotNull;

public enum AnimeSource {
    ANIME_OFFLINE_DATABASE("anime-offline-database (requires aod-extras)", false),
    MYANIMELIST("MyAnimeList (myanimelist.net)", true),
    ANILIST("AniList (anilist.co)", true),
    KITSU("Kitsu (kitsu.io)", true);

    private @NotNull final String displayString;
    private final boolean online;

    AnimeSource(@NotNull String displayString, boolean online) {
        this.displayString = displayString;
        this.online = online;
    }

    @Override
    public String toString() {
        return displayString;
    }

    public boolean isOnline() {
        return online;
    }
}
