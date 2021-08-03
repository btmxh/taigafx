package com.dah.taigafx.animelist;

import com.dah.taigafx.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class UserAnimeList {
    private String name;
    private ObservableList<UserAnime> animes;

    public UserAnimeList() {
    }

    public UserAnimeList(String name, ObservableList<UserAnime> animes) {
        this.name = name;
        this.animes = animes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservableList<UserAnime> getAnimes() {
        return animes;
    }

    public void setAnimes(List<UserAnime> animes) {
        this.animes = FXCollections.observableArrayList(animes);
    }

    public static UserAnimeList read(Path file) throws IOException {
        return Json.getObjectMapper().readValue(file.toFile(), UserAnimeList.class);
    }

    public static void write(UserAnimeList animeList, Path file) throws IOException {
        Json.getObjectMapper().writeValue(file.toFile(), animeList);
    }
}
