package com.dah.taigafx;

import com.dah.taigafx.data.anime.AnimeSource;
import com.dah.taigafx.api.AniListLoader;
import com.dah.taigafx.api.AnimeLoader;
import com.dah.taigafx.api.AodLoader;
import com.dah.taigafx.api.JikanLoader;
import com.dah.taigafx.config.Config;
import com.dah.taigafx.utils.BindingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.beans.binding.ObjectBinding;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

public class Provider {

    private final Config config;

    private final ThreadLocal<ObjectMapper> objectMapperProvider;
    private final JavaTimeModule javaTimeModule;
    private final ObjectBinding<AnimeLoader> animeLoader;
    private final HttpClient httpClient;

    public Provider(Config config) {
        this.config = Objects.requireNonNull(config);
        javaTimeModule = new JavaTimeModule();
        objectMapperProvider = ThreadLocal.withInitial(
                () -> new ObjectMapper().registerModule(javaTimeModule));
        animeLoader = BindingUtils.map(config.service().metadataProviderProperty(), this::createAnimeLoader);
        httpClient = HttpClient.newBuilder().build();
    }

    private AnimeLoader createAnimeLoader(AnimeSource source) {
        return switch(source) {
            case ANIME_OFFLINE_DATABASE -> new AodLoader(this);
            case MYANIMELIST -> new JikanLoader(this);
            case ANILIST -> new AniListLoader(this);
            default -> null;
        };
    }

    public @NotNull ObjectMapper getObjectMapper() {
        return objectMapperProvider.get();
    }

    public @NotNull Config getConfig() {
        return config;
    }

    public @NotNull HttpClient getHttpClient() {
        return httpClient;
    }

    public @NotNull Duration getConnectionTimeout() {
        return Duration.ofMinutes(2);
    }

    public AnimeLoader getAnimeLoader() {
        return animeLoader.get();
    }
}
