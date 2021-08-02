package com.dah.taigafx.animelist;

import com.dah.taigafx.anime.Anime;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserAnime {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private @NotNull Anime anime;
    private @NotNull SimpleIntegerProperty episodeWatched = new SimpleIntegerProperty();
    private @NotNull SimpleObjectProperty<@NotNull UserAnimeStatus> status = new SimpleObjectProperty<>();
    private @NotNull SimpleDoubleProperty score = new SimpleDoubleProperty(5.0);
    private @NotNull String notes = ""; // "" if empty
    private @Nullable LocalDate startDate, endDate;
    private @NotNull List<String> alternativeTitles = new ArrayList<>();
    private @Nullable String torrentSearchName;
    private @NotNull List<String> fansubGroupPreference = new ArrayList<>();
    private @Nullable Path torrentDownloadDirectory;

    // Do not use this.
    // This is for Jackson only
    @Deprecated
    public UserAnime() {}

    public UserAnime(@NotNull Anime anime, @NotNull SimpleIntegerProperty episodeWatched, @NotNull SimpleObjectProperty<@NotNull UserAnimeStatus> status, @NotNull SimpleDoubleProperty score, @NotNull String notes, @Nullable LocalDate startDate, @Nullable LocalDate endDate, @NotNull List<String> alternativeTitles, @Nullable String torrentSearchName, @NotNull List<String> fansubGroupPreference, @Nullable Path torrentDownloadDirectory) {
        this.anime = anime;
        this.episodeWatched = episodeWatched;
        this.status = status;
        this.score = score;
        this.notes = notes;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alternativeTitles = alternativeTitles;
        this.torrentSearchName = torrentSearchName;
        this.fansubGroupPreference = fansubGroupPreference;
        this.torrentDownloadDirectory = torrentDownloadDirectory;
    }

    public @NotNull Anime getAnime() {
        return anime;
    }

    public void setAnime(@NotNull Anime anime) {
        this.anime = anime;
    }

    public int getEpisodeWatched() {
        return episodeWatched.get();
    }

    public @NotNull SimpleIntegerProperty episodeWatchedProperty() {
        return episodeWatched;
    }

    public void setEpisodeWatched(int episodeWatched) {
        this.episodeWatched.set(episodeWatched);
    }

    public @NotNull UserAnimeStatus getStatus() {
        return status.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull UserAnimeStatus> statusProperty() {
        return status;
    }

    public void setStatus(@NotNull UserAnimeStatus status) {
        this.status.set(status);
    }

    public double getScore() {
        return score.get();
    }

    public @NotNull SimpleDoubleProperty scoreProperty() {
        return score;
    }

    public void setScore(double score) {
        this.score.set(score);
    }

    public @NotNull String getNotes() {
        return notes;
    }

    public void setNotes(@NotNull String notes) {
        this.notes = notes;
    }

    public @Nullable LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(@Nullable LocalDate startDate) {
        this.startDate = startDate;
    }

    public @Nullable LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(@Nullable LocalDate endDate) {
        this.endDate = endDate;
    }

    public @NotNull List<String> getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(@NotNull List<String> alternativeTitles) {
        this.alternativeTitles = alternativeTitles;
    }

    public @Nullable String getTorrentSearchName() {
        return torrentSearchName;
    }

    public void setTorrentSearchName(@Nullable String torrentSearchName) {
        this.torrentSearchName = torrentSearchName;
    }

    public @NotNull List<String> getFansubGroupPreference() {
        return fansubGroupPreference;
    }

    public void setFansubGroupPreference(@NotNull List<String> fansubGroupPreference) {
        this.fansubGroupPreference = fansubGroupPreference;
    }

    public @Nullable Path getTorrentDownloadDirectory() {
        return torrentDownloadDirectory;
    }

    public void setTorrentDownloadDirectory(@Nullable Path torrentDownloadDirectory) {
        this.torrentDownloadDirectory = torrentDownloadDirectory;
    }
}
