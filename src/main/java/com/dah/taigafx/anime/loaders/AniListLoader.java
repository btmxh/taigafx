package com.dah.taigafx.anime.loaders;

import com.dah.taigafx.Json;
import com.dah.taigafx.anime.*;
import com.dah.taigafx.exceptions.APIRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class AniListLoader extends BaseAnimeLoader {
    private static final String ANILIST_API_URL = "https://graphql.anilist.co";
    private static final String ANILIST_QUERY_STRING = """
            query ($id: Int) {
              Media (id: $id, type: ANIME) {
                id
                idMal
                title {
                  romaji
                  english
                  native
                }
                episodes
                synonyms
                format
                source
                status
                season
                seasonYear
                airingSchedule {
                  nodes {
                    episode
                    airingAt
                  }
                }
                duration
                startDate {
                  year
                  month
                  day
                }
                endDate {
                  year
                  month
                  day
                }
                averageScore
                description
                coverImage {
                  extraLarge
                  large
                  medium
                }
                genres
              }
            }
            """;

    public AniListLoader(@NotNull Duration timeout) {
        super(timeout);
    }

    public AniListLoader() {
    }

    @Override
    public CompletableFuture<Anime> loadAnime(String id) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ANILIST_API_URL))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(createBodyJSONQuery(id)))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
                .thenApply(r -> {
                    try {
                        var response = Json.getObjectMapper().readValue(r.body(), AniListResponse.class);
                        if(response.errors() == null || response.errors().isEmpty()) {
                            return response.data().media().toGenericAnime();
                        } else {
                            throw new CompletionException(new APIRequestException(
                                    response.errors().stream()
                                            .map(e -> new APIRequestException.Error(e.status(), e.message()))
                                            .collect(Collectors.toList())
                            ));
                        }
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    private String createBodyJSONQuery(String id) {
        var objectMapper = Json.getObjectMapper();
        var node = objectMapper.createObjectNode();
        var variables = objectMapper.createObjectNode();
        node.put("query", ANILIST_QUERY_STRING);
        node.set("variables", variables);
        variables.put("id", id);
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            // should not happen
            throw new IllegalStateException();
        }
    }

    public static record AniListResponse(
            List<AniListError> errors,AniListResponseData data) {}
    public static record AniListResponseData(
            @JsonProperty("Media") AniListAnime media
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record AniListError(int status, String message) {}

    public static record AniListAnime(
            int id,
            int idMal,
            AniListTitle title,
            List<String> synonyms,
            AniListFormat format,
            int episodes,
            AniListSource source,
            AniListStatus status,
            AnimeSeason season,
            int seasonYear,
            int duration,
            AniListDate startDate,
            AniListDate endDate,
            double averageScore,
            String description,
            AniListImages coverImage,
            List<String> genres,
            AniListSchedule airingSchedule
    ) {
        public Anime toGenericAnime() {
            var scheduledEpisodes = airingSchedule.nodes().stream()
                .mapToInt(AniListScheduleNode::episode)
                .max()
                .orElse(0);

            var schedule = new LocalDateTime[scheduledEpisodes];
            airingSchedule.nodes().forEach(node -> {
                var zone = TimeZone.getDefault().toZoneId();
                var instant = Instant.ofEpochSecond(node.airingAt());
                var dateTime = LocalDateTime.ofInstant(instant, zone);
                schedule[node.episode - 1] = dateTime;
            });
            return new Anime(
                    Map.of(
                            AnimeSource.ANILIST, "https://anilist.co/anime/" + id,
                            AnimeSource.MYANIMELIST, "https://myanimelist.net/anime/" + idMal
                    ),
                    title.romaji(),
                    title.english(),
                    title.japanese(),
                    synonyms,
                    format.getCorrespondingAnimeType(),
                    episodes,
                    status.getCorrespondingAnimeStatus(),
                    season == null? AnimeSeason.UNKNOWN : season,
                    seasonYear,
                    source.getDisplayString(),
                    startDate.toLocalDate(),
                    endDate.toLocalDate(),
                    averageScore * 0.1,
                    description,
                    List.of(
                            coverImage.extraLarge(),
                            coverImage.large,
                            coverImage.medium()
                    ),
                    genres,
                    Arrays.asList(schedule)
            );
        }
    }

    public static record AniListTitle(
            String romaji,
            String english,
            @JsonProperty("native") String japanese
    ) {}

    public static record AniListDate(
            int year,
            int month,
            int day
    ) {
        public @Nullable LocalDate toLocalDate() {
            if(year == 0 || month == 0 || day == 0) {
                // anime is not invented before 0 A.D
                return null;
            } else {
                return LocalDate.of(year, month, day);
            }
        }
    }
    
    public static record AniListImages(
            String extraLarge,
            String large,
            String medium
    ) {}

    public static record AniListSchedule(
            List<AniListScheduleNode> nodes
    ) {}

    public static record AniListScheduleNode(
            int episode,
            long airingAt
    ) {}

    @SuppressWarnings("unused")
    public enum AniListFormat {
        TV(AnimeType.TV),
        TV_SHORT(AnimeType.TV),
        MOVIE(AnimeType.MOVIE),
        SPECIAL(AnimeType.SPECIAL),
        OVA(AnimeType.OVA),
        ONA(AnimeType.ONA),
        MUSIC(AnimeType.MUSIC);

        private @NotNull final AnimeType correspondingAnimeType;

        AniListFormat(@NotNull AnimeType correspondingAnimeType) {
            this.correspondingAnimeType = correspondingAnimeType;
        }

        public @NotNull AnimeType getCorrespondingAnimeType() {
            return correspondingAnimeType;
        }
    }

    @SuppressWarnings("unused")
    public enum AniListStatus {
        FINISHED(AnimeStatus.COMPLETED),
        RELEASING(AnimeStatus.AIRING),
        NOT_YET_RELEASED(AnimeStatus.NOT_RELEASED),
        CANCELLED(AnimeStatus.CANCELLED),
        HIATUS(AnimeStatus.HIATUS);

        private @NotNull final AnimeStatus correspondingAnimeStatus;

        AniListStatus(@NotNull AnimeStatus correspondingAnimeStatus) {
            this.correspondingAnimeStatus = correspondingAnimeStatus;
        }

        public @NotNull AnimeStatus getCorrespondingAnimeStatus() {
            return correspondingAnimeStatus;
        }
    }

    @SuppressWarnings("unused")
    public enum AniListSource {
        ORIGINAL("Original"),
        MANGA("Manga"),
        LIGHT_NOVEL("Light novel"),
        VISUAL_NOVEL("Visual novel"),
        VIDEO_GAME("Game"),
        OTHER("Other"),
        NOVEL("Novel"),
        DOUJINSHI("Doujinshi"),
        ANIME("Anime");

        private @NotNull final String displayString;

        AniListSource(@NotNull String displayString) {
            this.displayString = displayString;
        }

        public @NotNull String getDisplayString() {
            return displayString;
        }
    }

}
