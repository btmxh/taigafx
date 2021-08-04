package com.dah.taigafx.api;

import com.dah.taigafx.data.SearchResult;
import com.dah.taigafx.data.anime.Anime;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public interface AnimeLoader {
    // AniList's anime schedule has the time when the anime has done airing,
    // but Jikan (and MyAnimeList) returns the time when the anime is broadcast.
    // This is why the program has to convert between the two time system.
    // To make the calculation more accurate, we should include the ad time
    // of Japanese televisions.
    Duration JAPANESE_TV_AD_TIME = Duration.ofMinutes(5);

    CompletableFuture<Anime> loadAnime(String id);

    CompletableFuture<SearchResult> searchAnime(String query, int page);

    class Itr implements Iterator<CompletableFuture<SearchResult>> {
        private @NotNull final AnimeLoader loader;
        private final String query;
        private SearchResult curr;
        private boolean loadedFirstPage = false;

        public Itr(@NotNull AnimeLoader loader, String query) {
            this.loader = loader;
            this.query = query;
        }

        @Override
        public boolean hasNext() {
            return !loadedFirstPage || (curr != null && curr.page().hasNext());
        }

        @Override
        public CompletableFuture<SearchResult> next() {
            if(curr == null) {
                return addCallback(loader.searchAnime(query, 0));
            } else if(!hasNext()) {
                throw new NoSuchElementException();
            } else {
                return addCallback(loader.searchAnime(query, curr.page().currentPage() + 1));
            }
        }

        private CompletableFuture<SearchResult> addCallback(CompletableFuture<SearchResult> future) {
            return future.whenComplete((result, ignore) -> {
                curr = result;
                loadedFirstPage = true;
            });
        }
    }

    default Iterator<CompletableFuture<SearchResult>> searchAnimeAsIterator(String query) {
        return new Itr(this, query);
    }
}
