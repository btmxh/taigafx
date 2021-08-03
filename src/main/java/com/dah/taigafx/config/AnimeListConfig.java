package com.dah.taigafx.config;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jetbrains.annotations.NotNull;

public class AnimeListConfig {
    private @NotNull final SimpleObjectProperty<@NotNull AnimeListAction>
            doubleClick = new SimpleObjectProperty<>(AnimeListAction.VIEW_INFORMATION),
            middleClick = new SimpleObjectProperty<>(AnimeListAction.PLAY_NEXT_EP),
            ctrlClick = new SimpleObjectProperty<>(AnimeListAction.DO_NOTHING),
            altClick = new SimpleObjectProperty<>(AnimeListAction.DO_NOTHING);
    private @NotNull final SimpleBooleanProperty
            highlightAnimeNewEps = new SimpleBooleanProperty(true),
            highlightAnimeNewDownloadEps = new SimpleBooleanProperty(true);

    public @NotNull AnimeListAction getDoubleClick() {
        return doubleClick.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull AnimeListAction> doubleClickProperty() {
        return doubleClick;
    }

    public void setDoubleClick(@NotNull AnimeListAction doubleClick) {
        this.doubleClick.set(doubleClick);
    }

    public @NotNull AnimeListAction getMiddleClick() {
        return middleClick.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull AnimeListAction> middleClickProperty() {
        return middleClick;
    }

    public void setMiddleClick(@NotNull AnimeListAction middleClick) {
        this.middleClick.set(middleClick);
    }

    public @NotNull AnimeListAction getCtrlClick() {
        return ctrlClick.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull AnimeListAction> ctrlClickProperty() {
        return ctrlClick;
    }

    public void setCtrlClick(@NotNull AnimeListAction ctrlClick) {
        this.ctrlClick.set(ctrlClick);
    }

    public @NotNull AnimeListAction getAltClick() {
        return altClick.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull AnimeListAction> altClickProperty() {
        return altClick;
    }

    public void setAltClick(@NotNull AnimeListAction altClick) {
        this.altClick.set(altClick);
    }

    public boolean isHighlightAnimeNewEps() {
        return highlightAnimeNewEps.get();
    }

    public @NotNull SimpleBooleanProperty highlightAnimeNewEpsProperty() {
        return highlightAnimeNewEps;
    }

    public void setHighlightAnimeNewEps(boolean highlightAnimeNewEps) {
        this.highlightAnimeNewEps.set(highlightAnimeNewEps);
    }

    public boolean isHighlightAnimeNewDownloadEps() {
        return highlightAnimeNewDownloadEps.get();
    }

    public @NotNull SimpleBooleanProperty highlightAnimeNewDownloadEpsProperty() {
        return highlightAnimeNewDownloadEps;
    }

    public void setHighlightAnimeNewDownloadEps(boolean highlightAnimeNewDownloadEps) {
        this.highlightAnimeNewDownloadEps.set(highlightAnimeNewDownloadEps);
    }

    public enum AnimeListAction {
        DO_NOTHING("Do nothing"),
        EDIT_USER_DETAILS("Edit user details (episodes watched, score, etc.)"),
        VIEW_INFORMATION("View anime information"),
        OPEN_FOLDER("Open anime folder"),
        PLAY_NEXT_EP("Play next episode"),
        VIEW_MAL_PAGE("View MyAnimeList page"),
        VIEW_AL_PAGE("View AniList page"),
        VIEW_KITSU_PAGE("View Kitsu page");

        private @NotNull final String displayString;

        AnimeListAction(@NotNull String displayString) {
            this.displayString = displayString;
        }

        @Override
        public @NotNull String toString() {
            return displayString;
        }
    }
}
