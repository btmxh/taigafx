package com.dah.taigafx.data.animelist;

import com.dah.taigafx.data.anime.Anime;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

public class UserAnime {
    @SuppressWarnings("NotNullFieldNotInitialized")
    private @NotNull Anime anime;
    private @NotNull final SimpleIntegerProperty episodeWatched
            = new SimpleIntegerProperty(0);
    private @NotNull final SimpleObjectProperty<@NotNull UserAnimeStatus> status
            = new SimpleObjectProperty<>(UserAnimeStatus.PLAN_TO_WATCH);
    private @NotNull final SimpleDoubleProperty score = new SimpleDoubleProperty(Double.NaN);
    private @NotNull String notes = ""; // "" if empty
    private @Nullable LocalDate startDate, endDate;
    // specify ArrayList to make sure that Jackson will create a mutable list
    private @NotNull ArrayList<String> alternativeTitles = new ArrayList<>();
    private @Nullable String torrentSearchName;
    private @NotNull ArrayList<String> fansubGroupPreference = new ArrayList<>();
    private @Nullable Path torrentDownloadDirectory;

    // Do not use this.
    // This is for Jackson only
    @Deprecated
    public UserAnime() {}

    public UserAnime(@NotNull Anime anime) {
        this.anime = anime;
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

    public @NotNull ArrayList<String> getAlternativeTitles() {
        return alternativeTitles;
    }

    public void setAlternativeTitles(@NotNull ArrayList<String> alternativeTitles) {
        this.alternativeTitles = alternativeTitles;
    }

    public @Nullable String getTorrentSearchName() {
        return torrentSearchName;
    }

    public void setTorrentSearchName(@Nullable String torrentSearchName) {
        this.torrentSearchName = torrentSearchName;
    }

    public @NotNull ArrayList<String> getFansubGroupPreference() {
        return fansubGroupPreference;
    }

    public void setFansubGroupPreference(@NotNull ArrayList<String> fansubGroupPreference) {
        this.fansubGroupPreference = fansubGroupPreference;
    }

    public @Nullable Path getTorrentDownloadDirectory() {
        return torrentDownloadDirectory;
    }

    public void setTorrentDownloadDirectory(@Nullable Path torrentDownloadDirectory) {
        this.torrentDownloadDirectory = torrentDownloadDirectory;
    }
}
