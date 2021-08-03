package com.dah.taigafx.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;

public record Config(
        ServiceConfig service,
        AnimeListConfig animeList
) {
    public Config() {
        this(new ServiceConfig(), new AnimeListConfig());
    }
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static Config read(Path configFile) throws IOException {
        return objectMapper.readValue(configFile.toFile(), Config.class);
    }

    public static void write(Config config, Path configFile) throws IOException {
        objectMapper.writeValue(configFile.toFile(), config);
    }
}
