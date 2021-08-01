package com.dah.taigafx.controllers;

import com.dah.taigafx.anime.AnimeSeason;
import com.dah.taigafx.anime.AnimeStatus;
import com.dah.taigafx.anime.AnimeType;
import com.dah.taigafx.animelist.UserAnime;
import com.dah.taigafx.animelist.UserAnimeStatus;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class MainWindowController {
    @FXML
    public AnchorPane allAnimePane;
    @FXML
    public AnchorPane currentlyWatchingPane;
    @FXML
    public AnchorPane completedPane;
    @FXML
    public AnchorPane onHoldPane;
    @FXML
    public AnchorPane droppedPane;
    @FXML
    public AnchorPane ptwPane;
    @FXML
    private AnchorPane sidebar;

    private final EnumMap<UserAnimeStatus, ObservableList<UserAnime>> filteredAnimeLists = new EnumMap<>(UserAnimeStatus.class);
    private ObservableList<UserAnime> animeList;

    @FXML
    public void initialize() {
        for(final var node : sidebar.getChildren()) {
            if(node instanceof JFXButton) {
                System.out.println(((JFXButton) node).getGraphic().getClass().toGenericString());
            }
        }

        initAnimePane(allAnimePane, null);
        initAnimePane(currentlyWatchingPane, UserAnimeStatus.WATCHING);
        initAnimePane(completedPane, UserAnimeStatus.COMPLETED);
        initAnimePane(onHoldPane, UserAnimeStatus.ON_HOLD);
        initAnimePane(droppedPane, UserAnimeStatus.DROPPED);
        initAnimePane(ptwPane, UserAnimeStatus.PLAN_TO_WATCH);
    }

    private void initAnimePane(@NotNull AnchorPane pane, @Nullable UserAnimeStatus status) {
        var list = FXCollections.<UserAnime>observableArrayList();
        if(status == null) {
            assert(animeList == null); // initAnimePane should not be called multiple times
            animeList = list;
        } else {
            var putResult = filteredAnimeLists.put(status, list);
            assert(putResult == null); // initAnimePane should not be called multiple times
        }
        System.out.println(pane.getWidth());

        var table = new TableView<UserAnime>();
        table.setItems(list);

        var animeStatusCol = new TableColumn<UserAnime, AnimeStatus>();
        var animeTitleCol = new TableColumn<UserAnime, String>();
        var userStatusCol = new TableColumn<UserAnime, UserAnime>();
        var animeTypeCol = new TableColumn<UserAnime, AnimeType>();
        var animeSeasonCol = new TableColumn<UserAnime, AnimeSeason.WithYear>();

        //noinspection unchecked
        table.getColumns().addAll(animeStatusCol, animeTitleCol, userStatusCol, animeTypeCol, animeSeasonCol);

        animeStatusCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().anime().status()));
        animeTitleCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().anime().title()));
        userStatusCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        animeTypeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().anime().type()));
        animeSeasonCol.setCellValueFactory(data -> {
            var anime = data.getValue().anime();
            var value = new AnimeSeason.WithYear(anime.season(), anime.seasonYear());
            return new ReadOnlyObjectWrapper<>(value);
        });
    }

    private void addAnimeToList(UserAnime anime) {
        animeList.add(anime);
        filteredAnimeLists.get(anime.status()).add(anime);
    }
}
