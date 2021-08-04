package com.dah.taigafx.data;

import com.dah.taigafx.data.anime.Anime;

import java.util.ArrayList;

public record SearchResult(SearchPage page, ArrayList<Anime> animes) {
    public static record SearchPage(
            int entriesPerPage,
            int currentPage,
            boolean hasNext
    ) {}
}
