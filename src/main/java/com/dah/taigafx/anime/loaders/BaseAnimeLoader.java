package com.dah.taigafx.anime.loaders;

import com.dah.taigafx.anime.Anime;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public abstract class BaseAnimeLoader extends BaseLoader {
    // AniList's anime schedule has the time when the anime has done airing,
    // but Jikan (and MyAnimeList) returns the time when the anime is broadcast.
    // This is why the program has to convert between the two time system.
    // To make the calculation more accurate, we should include the ad time
    // of Japanese televisions.
    public static final Duration JAPANESE_TV_AD_TIME = Duration.ofMinutes(5);

    public BaseAnimeLoader(@NotNull HttpClient httpClient, @NotNull Duration timeout) {
        super(httpClient, timeout);
    }

    public BaseAnimeLoader(@NotNull Duration timeout) {
        super(timeout);
    }

    public BaseAnimeLoader() {
    }

    public abstract CompletableFuture<Anime> loadAnime(String id);
}
