package com.dah.taigafx.anime.loaders;

import com.dah.taigafx.Provider;
import com.dah.taigafx.anime.*;
import com.dah.taigafx.exceptions.APIRequestException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JikanLoader extends BaseLoader implements AnimeLoader {
    private @NotNull final ObjectMapper objectMapper;
    public JikanLoader(@NotNull Provider provider) {
        super(provider);
        objectMapper = provider.getObjectMapper().copy();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public @NotNull ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public JsonNode handleJikanError(JsonNode tree) throws CompletionException {
        try {
            var data = tree.get("data");
            if(data == null) {
                // error occurred
                var error = objectMapper.treeToValue(tree, JikanErrorResponse.class);
                var genericError = new APIRequestException.Error(error.status(), error.message());
                throw new CompletionException(new APIRequestException(List.of(genericError)));
            } else {
                return data;
            }
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
    }

    @Override
    public CompletableFuture<Anime> loadAnime(String id) {
        var request = buildRequest("https://api.jikan.moe/v4/anime/" + id).GET().build();
        return getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        var tree = objectMapper.readTree(response.body());
                        return objectMapper.treeToValue(handleJikanError(tree), JikanAnime.class).toGenericAnime();
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    @Override
    public CompletableFuture<SearchResult> searchAnime(String query, int page) {
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);
        var url = "https://api.jikan.moe/v4/anime?q=" + query + "&page=" + (page + 1);
        var request = buildRequest(url).GET().build();
        return getHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        var tree = objectMapper.readTree(response.body());
                        handleJikanError(tree);
                        return objectMapper.treeToValue(tree, JikanSearchResponse.class).toSearchResult(page);
                    } catch (JsonProcessingException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record JikanErrorResponse(int status, String message) {}
    public static record JikanSearchResponse(JikanPagination pagination, List<JikanAnime> data) {
        public SearchResult toSearchResult(int currentPage) {
            return new SearchResult(
                    new SearchResult.SearchPage(data.size(), currentPage, pagination.hasNext()),
                    data.stream().map(JikanAnime::toGenericAnime).collect(Collectors.toCollection(ArrayList::new))
            );
        }
    }

    public static record JikanPagination(
            @JsonProperty("last_visible_page") int pageCount,
            @JsonProperty("has_next_page") boolean hasNext
    ) {}

    public static record JikanAnime(
            @JsonProperty("mal_id") int malID,
            JikanImages images,
            String title,
            @JsonProperty("title_english") String titleEng,
            @JsonProperty("title_japanese") String titleJpn,
            @JsonProperty("title_synonyms") List<String> synonyms,
            String type,
            String source,
            int episodes,
            String status,
            JikanAiredInfo aired,
            String season,
            JikanBroadcast broadcast,
            int year,
            double score,
            String background,
            List<JikanGenre> genres,
            String duration
    ) {
        public Anime toGenericAnime() {
            return new Anime(
                    Map.of(
                            AnimeSource.MYANIMELIST, "https://myanimelist.net/anime/" + malID(),
                            // MAL is top priority on aod-extras
                            AnimeSource.ANIME_OFFLINE_DATABASE, "https://myanimelist.net/anime/" + malID()
                    ),
                    title,
                    titleEng,
                    titleJpn,
                    synonyms,
                    switch(type) {
                        case "TV" -> AnimeType.TV;
                        case "Movie" -> AnimeType.MOVIE;
                        case "OVA" -> AnimeType.OVA;
                        case "ONA" -> AnimeType.ONA;
                        case "Music" -> AnimeType.MUSIC;
                        case "Special" -> AnimeType.SPECIAL;
                        default -> AnimeType.UNKNOWN;
                    },
                    episodes,
                    switch(status) {
                        case "Finished Airing" -> AnimeStatus.COMPLETED;
                        case "Currently Airing" -> AnimeStatus.AIRING;
                        case "Not yet aired" -> AnimeStatus.NOT_RELEASED;
                        default -> AnimeStatus.UNKNOWN;
                    },
                    season == null? AnimeSeason.UNKNOWN : switch(season) {
                        case "spring" -> AnimeSeason.SPRING;
                        case "summer" -> AnimeSeason.SUMMER;
                        case "fall" -> AnimeSeason.FALL;
                        case "winter" -> AnimeSeason.WINTER;
                        default -> AnimeSeason.UNKNOWN;
                    },
                    year,
                    source,
                    parseDateTimeString(aired.from()),
                    parseDateTimeString(aired.to()),
                    score,
                    background,
                    images.jpg().getImageUrls(),
                    genres.stream().map(JikanGenre::name).collect(Collectors.toList()),
                    getSchedule()
            );
        }

        private @Nullable LocalDate parseDateTimeString(@Nullable String dateTimeString) {
            if(dateTimeString == null) return null;
            return ZonedDateTime.parse(dateTimeString)
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate();
        }

        private List<String> getImages() {
            return null;
        }

        private List<LocalDateTime> getSchedule() {
            if(broadcast != null && broadcast.timezone != null && broadcast.time != null && aired.from() != null) {
                var zone = ZoneId.of(broadcast.timezone());
                var time = LocalTime.parse(broadcast.time());
                // In the Jikan API, aired.from doesn't contains the time
                var start = ZonedDateTime.parse(aired.from()).toLocalDate().atTime(time);
                var episodeLength = getDuration();
                var episodeCombinedLength = episodeLength == null?
                        Duration.ZERO : episodeLength.plus(JAPANESE_TV_AD_TIME);
                // Assuming the anime is aired weekly. Therefore, delays will not be counted
                // (for example: Love Live Superstar got delayed because of Olympic 2020)
                return IntStream.range(0, episodes)
                        .mapToObj(ep -> start.plus(ep, ChronoUnit.WEEKS))
                        .map(dateTime -> dateTime.atZone(zone)
                                .withZoneSameInstant(ZoneId.systemDefault())
                                .plus(episodeCombinedLength)
                                .toLocalDateTime())
                        .collect(Collectors.toList());
            } else {
                return Arrays.asList(new LocalDateTime[episodes]);
            }
        }

        public @Nullable Duration getDuration() {
            // Most MyAnimeList animes has its duration property to be something like "%d min per ep"
            // To make things generic, we will retrieve all number sequences in the string,
            // and get the number which is most likely to be the number of minutes in one episode

            List<String> tokens = new ArrayList<>(Arrays.asList(duration.split("\\s+")));
            OptionalInt firstIntSequence = tokens.stream()
                    .filter(token -> token.chars().allMatch(c -> c >= '0' && c <= '9'))
                    .mapToInt(Integer::parseInt).findFirst();
            if(firstIntSequence.isEmpty()) {
                return null;
            }
            if(tokens.removeAll(Set.of("min", "min.", "minute", "minutes"))) {
                return Duration.ofMinutes(firstIntSequence.getAsInt());
            } else if(tokens.removeAll(Set.of("sec", "sec.", "second", "seconds"))) {
                return Duration.ofSeconds(firstIntSequence.getAsInt());
            } else if(tokens.removeAll(Set.of("hour", "hr", "hours"))) {
                return Duration.ofHours(firstIntSequence.getAsInt());
            } else {
                return null;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record JikanAiredInfo(String from, String to) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record JikanGenre(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record JikanBroadcast(String time, String timezone) {}

    // JavaFX doesn't support webp (tbh idk)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record JikanImages(JikanImageUrls jpg) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record JikanImageUrls(
            @JsonProperty("image_url") String medium,
            @JsonProperty("small_image_url") String small,
            @JsonProperty("large_image_url") String large
    ) {
        public List<String> getImageUrls() {
            return List.of(large, medium, small);
        }
    }
}
