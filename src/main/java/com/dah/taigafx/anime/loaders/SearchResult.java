package com.dah.taigafx.anime.loaders;

import com.dah.taigafx.anime.Anime;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public record SearchResult(SearchPage page, ArrayList<Anime> animes) {
    public static record SearchPage(
            int entriesPerPage,
            int currentPage,
            boolean hasNext
    ) {}
}
