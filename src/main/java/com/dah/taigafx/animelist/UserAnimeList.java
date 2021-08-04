package com.dah.taigafx.animelist;

import com.dah.taigafx.Provider;
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

    public static UserAnimeList read(@NotNull Provider provider, @NotNull Path file) throws IOException {
        return provider.getObjectMapper().readValue(file.toFile(), UserAnimeList.class);
    }

    public static void write(@NotNull Provider provider,
                             @NotNull UserAnimeList animeList,
                             @NotNull Path file) throws IOException {
        provider.getObjectMapper().writeValue(file.toFile(), animeList);
    }
}
