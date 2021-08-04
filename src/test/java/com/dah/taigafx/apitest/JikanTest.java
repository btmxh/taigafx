package com.dah.taigafx.apitest;

import com.dah.taigafx.Provider;
import com.dah.taigafx.anime.AnimeSeason;
import com.dah.taigafx.anime.loaders.AniListLoader;
import com.dah.taigafx.anime.loaders.AnimeLoader;
import com.dah.taigafx.anime.loaders.JikanLoader;
import com.dah.taigafx.config.Config;
import com.dah.taigafx.exceptions.APIRequestException;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("CommentedOutCode")
public class JikanTest {
    private final Provider provider = new Provider(new Config());
    private final AnimeLoader loader = new JikanLoader(provider);
    @Test
    public void testBokurema() throws ExecutionException, InterruptedException {
        final var id = "40904";

        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Bokutachi no Remake");
        assertEquals(anime.episodes(), 12);
    }

    // not released, no season infos
    @Test
    public void testMagirecoS3() throws ExecutionException, InterruptedException {
        final var id = "49291";

        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Magia Record: Mahou Shoujo Madoka☆Magica Gaiden (TV) Final Season: Asaki Yume no Akatsuki");
        assertEquals(anime.season(), AnimeSeason.UNKNOWN);
    }

    @Test
    public void testFailure() {
        final var id = "0";
        var response = loader.loadAnime(id);
        try {
            response.join();
            throw new IllegalStateException();
        } catch (CompletionException ex) {
            var cause = ex.getCause();
            assertTrue(cause instanceof APIRequestException);
        }
    }

    // turn off your internet connection to do this test
    /*
    @Test
    public void testNoInternet() {
        final var id = "0";
        var loader = new AniListLoader();
        var response = loader.loadAnime(id);
        try {
            response.join();
            throw new IllegalStateException();
        } catch (CompletionException ex) {
            var cause = ex.getCause();
            assertTrue(cause instanceof ConnectException);
        }
    }
    */

    @Test
    public void testTimeout() {
        final var id = "0";
        var response = loader.loadAnime(id).orTimeout(1, TimeUnit.NANOSECONDS);
        try {
            response.join();
            throw new IllegalStateException();
        } catch (CompletionException ex) {
            var cause = ex.getCause();
            assertTrue(cause instanceof HttpTimeoutException);
        }
    }

    @Test
    public void testSearch() throws ExecutionException, InterruptedException {
        final var query = "Oregairu";
        var response = loader.searchAnime(query, 0).get();
        var first = response.animes().get(0);

        assertTrue(first.title().contains("Yahari Ore"));
        assertTrue(Objects.requireNonNull(first.titleEng()).contains("Comedy"));
    }

    @Test
    public void testSearchSpaces() throws ExecutionException, InterruptedException {
        final var query = "kokoro con";
        var response = loader.searchAnime(query, 0).get();
        var first = response.animes().get(0);

        assertTrue(first.title().contains("Connect"));
        assertTrue(first.genres().contains("Drama"));
    }

    @Test
    public void testSearchUnicode() throws ExecutionException, InterruptedException {
        final var query = "madoka\u2605";
        var response = loader.searchAnime(query, 0).get();
        var first = response.animes().get(0);

        assertTrue(first.title().contains("Magica"));
        assertTrue(Objects.requireNonNull(first.titleEng()).contains("Puella"));
    }
}
