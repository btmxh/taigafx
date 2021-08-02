package com.dah.taigafx.animelist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public record UserAnimeList(
        String name,
        ObservableList<UserAnime> animes
) {
    public static record UserAnimeListJSON(String name, List<UserAnime> animes) {
        public UserAnimeList toUserAnimeList() {
            return new UserAnimeList(name, FXCollections.observableArrayList(animes));
        }
    }

    private static final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static UserAnimeList read(Path file) throws IOException {
        return objectMapper.readValue(file.toFile(), UserAnimeListJSON.class)
                .toUserAnimeList();
    }

    public static void write(UserAnimeList animeList, Path file) throws IOException {
        objectMapper.writeValue(file.toFile(), new UserAnimeListJSON(animeList.name, animeList.animes));
    }
}
