package com.dah.taigafx.apitest;

import com.dah.taigafx.anime.AnimeSeason;
import com.dah.taigafx.anime.loaders.AniListLoader;
import com.dah.taigafx.anime.loaders.JikanLoader;
import com.dah.taigafx.exceptions.APIRequestException;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("CommentedOutCode")
public class JikanTest {
    @Test
    public void testBokurema() throws ExecutionException, InterruptedException {
        final var id = "40904";

        var loader = new JikanLoader();
        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Bokutachi no Remake");
        assertEquals(anime.episodes(), 12);
    }

    // not released, no season infos
    @Test
    public void testMagirecoS3() throws ExecutionException, InterruptedException {
        final var id = "49291";

        var loader = new JikanLoader();
        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Magia Record: Mahou Shoujo Madoka☆Magica Gaiden (TV) Final Season: Asaki Yume no Akatsuki");
        assertEquals(anime.season(), AnimeSeason.UNKNOWN);
    }

    @Test
    public void testFailure() {
        final var id = "0";
        var loader = new JikanLoader();
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
        var loader = new JikanLoader(Duration.ofNanos(1));
        var response = loader.loadAnime(id);
        try {
            response.join();
            throw new IllegalStateException();
        } catch (CompletionException ex) {
            var cause = ex.getCause();
            assertTrue(cause instanceof HttpTimeoutException);
        }
    }
}
