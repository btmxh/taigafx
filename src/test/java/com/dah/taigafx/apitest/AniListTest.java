package com.dah.taigafx.apitest;

import com.dah.taigafx.Provider;
import com.dah.taigafx.data.anime.AnimeSeason;
import com.dah.taigafx.api.AniListLoader;
import com.dah.taigafx.api.AnimeLoader;
import com.dah.taigafx.config.Config;
import com.dah.taigafx.exceptions.APIRequestException;
import org.junit.jupiter.api.Test;

import java.net.http.HttpTimeoutException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("CommentedOutCode")
public class AniListTest {
    private final Provider provider = new Provider(new Config());
    private final AnimeLoader loader = new AniListLoader(provider);
    @Test
    public void testBokurema() throws ExecutionException, InterruptedException {
        final var id = "114065";

        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Bokutachi no Remake");
        assertEquals(anime.episodes(), 12);
    }

    // not released, no season infos
    @Test
    public void testMagirecoS3() throws ExecutionException, InterruptedException {
        final var id = "136080";

        var anime = loader.loadAnime(id).get();

        assertEquals(anime.title(), "Magia Record: Mahou Shoujo Madoka☆Magica Gaiden Final Season - Asaki Yume no Akatsuki");
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
}
