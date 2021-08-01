package com.dah.taigafx.apitest;

import com.dah.taigafx.anime.AnimeSeason;
import com.dah.taigafx.anime.loaders.AniListLoader;
import com.dah.taigafx.exceptions.APIRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("CommentedOutCode")
public class AniListTest {
    @Test
    public void testBokurema() throws ExecutionException, InterruptedException {
        final int id = 114065;

        var loader = new AniListLoader();
        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Bokutachi no Remake");
        assertEquals(anime.episodes(), 12);
    }

    // not released, no season infos
    @Test
    public void testMagirecoS3() throws ExecutionException, InterruptedException {
        final int id = 136080;

        var loader = new AniListLoader();
        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Magia Record: Mahou Shoujo Madoka☆Magica Gaiden Final Season - Asaki Yume no Akatsuki");
        assertEquals(anime.season(), AnimeSeason.UNKNOWN);
    }

    @Test
    public void testFailure() {
        final int id = 0;
        var loader = new AniListLoader();
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
        final int id = 0;
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
        final int id = 0;
        var loader = new AniListLoader(Duration.ofNanos(1));
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
