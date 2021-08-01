package com.dah.taigafx.anime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Modelled after the JSON output from Jikan API
public record Anime(
        @NotNull Map<AnimeSource, String> urls,
        @NotNull String title,
        @Nullable String titleEng,
        @Nullable String titleJpn,
        @NotNull List<String> titleSynonyms,
        @NotNull AnimeType type,
        int episodes,
        @NotNull AnimeStatus status,
        @NotNull AnimeSeason season,
        int seasonYear,
        @NotNull String source,
        @Nullable LocalDate startDate,
        @Nullable LocalDate endDate,
        double score,                           // out of 10
        @NotNull String description,            // in HTML
        @NotNull List<String> imageUrls,        // ordered by image resolution
        @NotNull List<String> genres,
        @NotNull List<LocalDateTime> schedule   // release datetime for every episodes
                                                // Note: this is the time when the episode finished airing
) {}
