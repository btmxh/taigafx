package com.dah.taigafx.config;

import com.dah.taigafx.Json;

import java.io.IOException;
import java.nio.file.Path;

public record Config(
        ServiceConfig service,
        AnimeListConfig animeList
) {
    public Config() {
        this(new ServiceConfig(), new AnimeListConfig());
    }

    public static Config read(Path configFile) throws IOException {
        return Json.getObjectMapper().readValue(configFile.toFile(), Config.class);
    }

    public static void write(Config config, Path configFile) throws IOException {
        Json.getObjectMapper().writeValue(configFile.toFile(), config);
    }
}
