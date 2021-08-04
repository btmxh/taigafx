package com.dah.taigafx.config;

import com.dah.taigafx.Provider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;

public record Config(
        ServiceConfig service,
        AnimeListConfig animeList
) {
    public Config() {
        this(new ServiceConfig(), new AnimeListConfig());
    }

    public static Config read(@NotNull Provider provider, @NotNull Path configFile) throws IOException {
        return provider.getObjectMapper().readValue(configFile.toFile(), Config.class);
    }

    public static void write(@NotNull Provider provider,
                             @NotNull Config config,
                             @NotNull Path configFile) throws IOException {
        provider.getObjectMapper().writeValue(configFile.toFile(), config);
    }
}
