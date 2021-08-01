package com.dah.taigafx.animelist;

import com.dah.taigafx.anime.Anime;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public record UserAnime(
        Anime anime,
        int episodeWatched,
        UserAnimeStatus status,
        double score,
        String notes,
        LocalDate startDate,
        LocalDate endDate,

        // torrent stuff
        List<String> alternativeTitles,
        String torrentSearchName,
        List<String> fansubGroupPreference,
        Path torrentDownloadDirectory
) {}
