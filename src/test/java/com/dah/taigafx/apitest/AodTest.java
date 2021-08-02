package com.dah.taigafx.apitest;

import com.dah.taigafx.anime.AnimeSeason;
import com.dah.taigafx.anime.loaders.AniListLoader;
import com.dah.taigafx.anime.loaders.AodLoader;
import com.dah.taigafx.exceptions.APIRequestException;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AodTest {
    public static final String NULL_AOD_EXTRAS_PATH = "";

    // change NULL_AOD_EXTRAS_PATH to your aod-extras path to do this test
    public static final Path AOD_EXTRAS_PATH = Path.of(NULL_AOD_EXTRAS_PATH);

    @Test
    public void testBokurema() throws ExecutionException, InterruptedException {
        if(AOD_EXTRAS_PATH.toString().equals(NULL_AOD_EXTRAS_PATH)) return;
        final var id = "https://myanimelist.net/anime/40904";

        var loader = new AodLoader(AOD_EXTRAS_PATH);
        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Bokutachi no Remake");
        assertEquals(anime.episodes(), 12);
    }

    // not released
    @Test
    public void testMagirecoS3() throws ExecutionException, InterruptedException {
        if(AOD_EXTRAS_PATH.toString().equals(NULL_AOD_EXTRAS_PATH)) return;
        final var id = "https://myanimelist.net/anime/49291";

        var loader = new AodLoader(AOD_EXTRAS_PATH);
        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Magia Record: Mahou Shoujo Madoka☆Magica Gaiden (TV) Final Season: Asaki Yume no Akatsuki");
        assertEquals(anime.season(), AnimeSeason.FALL);
    }

    @Test
    public void testFailure() {
        if(AOD_EXTRAS_PATH.toString().equals(NULL_AOD_EXTRAS_PATH)) return;
        final var id = "0";
        var loader = new AodLoader(AOD_EXTRAS_PATH);
        var response = loader.loadAnime(id);
        try {
            response.join();
            throw new IllegalStateException();
        } catch (CompletionException ex) {
            var cause = ex.getCause();
            assertTrue(cause instanceof APIRequestException);
        }
    }

    @Test
    public void testTimeout() {
        if(AOD_EXTRAS_PATH.toString().equals(NULL_AOD_EXTRAS_PATH)) return;
        final var id = "0";
        var loader = new AodLoader(AOD_EXTRAS_PATH, Duration.ofNanos(1));
        var response = loader.loadAnime(id);
        try {
            response.join();
            throw new IllegalStateException();
        } catch (CompletionException ex) {
            var cause = ex.getCause();
            assertTrue(cause instanceof TimeoutException);
        }
    }
}
