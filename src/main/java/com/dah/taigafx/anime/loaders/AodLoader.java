package com.dah.taigafx.anime.loaders;

import com.dah.taigafx.anime.*;
import com.dah.taigafx.exceptions.APIRequestException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class AodLoader extends BaseAnimeLoader {
    private @NotNull final Path aodExtrasPath;
    public AodLoader(@NotNull Path aodExtrasPath, @NotNull Duration timeout) {
        super(timeout);
        this.aodExtrasPath = aodExtrasPath;
    }

    public AodLoader(@NotNull Path aodExtrasPath) {
        this.aodExtrasPath = aodExtrasPath;
    }

    @Override
    public CompletableFuture<Anime> loadAnime(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var hash = MessageDigest.getInstance("SHA-1");
                var bytes = hash.digest(id.getBytes(StandardCharsets.UTF_8));
                var first8Chars = Arrays.copyOf(bytes, 4); // a hex char = 1/2 byte
                var bucket = (((first8Chars[0] & 0xff) << 24)
                        | ((first8Chars[1] & 0xff) << 16)
                        | ((first8Chars[2] & 0xff) << 8)
                        | ((first8Chars[3] & 0xff))) & 1023;
                var filename = aodExtrasPath.resolve("minidb")
                        .resolve(bucket + ".json").toAbsolutePath().toString();
                var data = objectMapper.readValue(new File(filename), AodData.class);
                var result = data.data.stream()
                        .filter(a -> a.id().equals(id))
                        .findFirst();
                if(result.isEmpty()) {
                    var msg = "no entry with id '" + id + "' in bucket file '" + filename + "'";
                    var error = new APIRequestException.Error(404, msg);
                    throw new CompletionException(new APIRequestException(List.of(error)));
                } else {
                    return result.get().toGenericAnime();
                }

            } catch (NoSuchAlgorithmException e) {
                // never throws
                throw new IllegalStateException();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }).orTimeout(timeout.getNano() + timeout.getSeconds() * 1_000_000_000, TimeUnit.NANOSECONDS);
    }

    public static record AodData(List<AodAnime> data) {}
    public static record AodAnime(
            @NotNull List<String> sources,
            @NotNull String title,
            @NotNull AnimeType type,
            int episodes,
            @NotNull AodStatus status,
            @NotNull AodAnimeSeason animeSeason,
            @NotNull String picture,
            @NotNull String thumbnail,
            @NotNull List<String> synonyms,
            @NotNull List<String> relations,
            @NotNull List<String> tags,
            @NotNull String id
    ) {
        public Anime toGenericAnime() {
            return new Anime(
                    getSourceUrlMap(),
                    title,
                    null,
                    null,
                    synonyms,
                    type,
                    episodes,
                    switch(status) {
                        case UPCOMING -> AnimeStatus.NOT_RELEASED;
                        case ONGOING -> AnimeStatus.AIRING;
                        case UNKNOWN -> AnimeStatus.UNKNOWN;
                        case FINISHED -> AnimeStatus.COMPLETED;
                    },
                    switch(animeSeason.season) {
                        case SPRING -> AnimeSeason.SPRING;
                        case SUMMER -> AnimeSeason.SUMMER;
                        case FALL -> AnimeSeason.FALL;
                        case WINTER -> AnimeSeason.WINTER;
                        case UNDEFINED -> AnimeSeason.UNKNOWN;
                    },
                    animeSeason.year,
                    guessSource(),
                    null,
                    null,
                    5.0,
                    "",
                    List.of(picture),
                    guessGenres(),
                    Arrays.asList(new LocalDateTime[episodes])
            );
        }

        private List<String> guessGenres() {
            final String[] genres = {
                    // MyAnimeList genres
                    "Action", "Adventure", "Cars", "Comedy", "Mystery", "Dementia",
                    "Drama", "Ecchi", "Fantasy", "Game", "Hentai", "Historical",
                    "Horror", "Kids", "Magic", "Martial Arts", "Mecha", "Music",
                    "Parody", "Samurai", "Romance", "School", "Sci Fi", "Shoujo",
                    "Shoujo Ai", "Shounen", "Shounen Ai", "Space", "Sports",
                    "Super Power", "Vampire", "Yaoi", "Yuri", "Harem",
                    "Slice Of Life", "Supernatural", "Military", "Police",
                    "Psychological", "Thriller", "Seinen", "Josei"
            };

            return Arrays.stream(genres)
                    .filter(genre -> tags.contains(genre.toLowerCase()))
                    .toList();
        }

        private @NotNull String guessSource() {
            if(tags.contains("based on a light novel")) {
                return "Light novel";
            } else if(tags.contains("based on a manga")) {
                return "Manga";
            } else if(tags.contains("based on a movie")) {
                return "Movie";
            } else if(tags.contains("based on a mobile game")) {
                return "Game";
            } else if(tags.contains("based on a visual novel")
                    || tags.contains("based on a eroge")) {
                return "Visual novel";
            } else if(tags.contains("based on a fairy tale")) {
                return "Fairy tale";
            } else return "Unknown";
        }

        private @NotNull Map<AnimeSource, String> getSourceUrlMap() {
            var map = new HashMap<AnimeSource, String>();
            for(final var source : sources) {
                if(source.contains("myanimelist.net")) {
                    map.put(AnimeSource.MYANIMELIST, source);
                } else if(source.contains("anilist.co")) {
                    map.put(AnimeSource.ANILIST, source);
                } else {
                    map.put(AnimeSource.KITSU, source);
                }
            }
            return map;
        }
    }

    public static record AodAnimeSeason(AodSeason season, int year) {}

    public enum AodStatus {
        FINISHED, ONGOING, UPCOMING, UNKNOWN
    }

    public enum AodSeason {
        SPRING, SUMMER, FALL, WINTER, UNDEFINED
    }
}
