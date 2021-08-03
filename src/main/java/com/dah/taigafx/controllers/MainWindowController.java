package com.dah.taigafx.controllers;

import com.dah.taigafx.anime.AnimeSeason;
import com.dah.taigafx.anime.AnimeStatus;
import com.dah.taigafx.anime.AnimeType;
import com.dah.taigafx.animelist.UserAnime;
import com.dah.taigafx.animelist.UserAnimeList;
import com.dah.taigafx.animelist.UserAnimeStatus;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.SegmentedBar;
import org.controlsfx.dialog.ExceptionDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

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

    private final EnumMap<UserAnimeStatus, ObservableList<UserAnime>> filteredAnimeLists
            = new EnumMap<>(UserAnimeStatus.class);
    private UserAnimeList animeList;

    @FXML
    public void initialize() throws IOException {
        animeList = UserAnimeList.read(Path.of("animelist.json"));
        for(final var status : UserAnimeStatus.values()) {
            filteredAnimeLists.put(status, FXCollections.observableArrayList());
        }

        for(final var anime : animeList.animes()) {
            filteredAnimeLists.get(anime.getStatus()).add(anime);
        }

        initAnimePane(allAnimePane, null);
        initAnimePane(currentlyWatchingPane, UserAnimeStatus.WATCHING);
        initAnimePane(completedPane, UserAnimeStatus.COMPLETED);
        initAnimePane(onHoldPane, UserAnimeStatus.ON_HOLD);
        initAnimePane(droppedPane, UserAnimeStatus.DROPPED);
        initAnimePane(ptwPane, UserAnimeStatus.PLAN_TO_WATCH);
    }

    private void initAnimePane(@NotNull AnchorPane pane, @Nullable UserAnimeStatus status) {
        var list = status == null? animeList.animes() :
                filteredAnimeLists.get(status);

        var table = new TableView<UserAnime>();
        table.getStyleClass().add("anime-list-table");
        table.setItems(list);

        var animeStatusCol = new TableColumn<UserAnime, AnimeStatus>();
        var animeTitleCol = new TableColumn<UserAnime, String>("Anime Title");
        var userStatusCol = new TableColumn<UserAnime, UserAnime>("Status");
        var animeTypeCol = new TableColumn<UserAnime, AnimeType>("Type");
        var animeSeasonCol = new TableColumn<UserAnime, AnimeSeason.WithYear>("Season");

        table.getColumns().add(animeStatusCol);
        table.getColumns().add(animeTitleCol);
        table.getColumns().add(userStatusCol);
        table.getColumns().add(animeTypeCol);
        table.getColumns().add(animeSeasonCol);

        animeStatusCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAnime().status()));
        animeTitleCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAnime().title()));
        userStatusCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        animeTypeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getAnime().type()));

        animeSeasonCol.setCellValueFactory(data -> {
            var anime = data.getValue().getAnime();
            var value = new AnimeSeason.WithYear(anime.season(), anime.seasonYear());
            return new ReadOnlyObjectWrapper<>(value);
        });

        animeStatusCol.getStyleClass().add("anime-status-col");
        animeTitleCol.getStyleClass().add("anime-title-col");
        userStatusCol.getStyleClass().add("user-status-col");
        animeTypeCol.getStyleClass().add("anime-type-col");
        animeSeasonCol.getStyleClass().add("anime-season-col");

        animeStatusCol.setPrefWidth(20.0);
        animeTitleCol.setPrefWidth(630.0);
        userStatusCol.setPrefWidth(200.0);
        animeTypeCol.setPrefWidth(80.0);
        animeSeasonCol.setPrefWidth(100.0);

        animeStatusCol.setCellFactory(col -> new TableCell<>(){
            private static final Map<AnimeStatus, PseudoClass> statusPseudoClasses = Map.of(
                    AnimeStatus.AIRING, PseudoClass.getPseudoClass("airing"),
                    AnimeStatus.COMPLETED, PseudoClass.getPseudoClass("completed"),
                    AnimeStatus.UNKNOWN, PseudoClass.getPseudoClass("unknown"),
                    AnimeStatus.CANCELLED, PseudoClass.getPseudoClass("cancelled"),
                    AnimeStatus.HIATUS, PseudoClass.getPseudoClass("hiatus"),
                    AnimeStatus.NOT_RELEASED, PseudoClass.getPseudoClass("not-released")
            );
            private final Region rect = new Region();
            {
                rect.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                rect.getStyleClass().add("anime-status-display");
            }
            @Override
            protected void updateItem(AnimeStatus item, boolean empty) {
                super.updateItem(item, empty);
                statusPseudoClasses.values().forEach(pseudoClass ->
                        rect.pseudoClassStateChanged(pseudoClass, false));
                if(item != null && !empty) {
                    setGraphic(rect);
                    rect.pseudoClassStateChanged(statusPseudoClasses.get(item), true);
                } else {
                    setGraphic(null);
                }
            }
        });

        userStatusCol.setCellFactory(col -> new TableCell<>(){
            private static final PseudoClass
                WATCHED_CLASS = PseudoClass.getPseudoClass("watched"),
                RELEASED_CLASS = PseudoClass.getPseudoClass("released"),
                NOT_AIRED_CLASS = PseudoClass.getPseudoClass("not-aired");

            private final HBox graphic;
            private final SegmentedBar<SegmentedBar.Segment> statusBar = new SegmentedBar<>();
            private final SegmentedBar.Segment watched, released, notAired;
            private final Label label;

            {
                graphic = new HBox(2.0);
                watched = new SegmentedBar.Segment(0.0, "");
                released = new SegmentedBar.Segment(0.0, "");
                notAired = new SegmentedBar.Segment(1.0, "");
                statusBar.getSegments().addAll(watched, released, notAired);
                statusBar.setMinWidth(150.0);
                statusBar.setMinHeight(16.0);
                statusBar.setSegmentViewFactory(segment -> {
                    var region = new Control() {
                        @Override
                        protected Skin<?> createDefaultSkin() {
                            return new SkinBase<Control>(this) {};
                        }
                    };

                    region.getStyleClass().add("status-bar-segment");
                    if(segment == watched) {
                        region.pseudoClassStateChanged(WATCHED_CLASS, true);
                        region.setTooltip(new Tooltip("Watched"));
                    } else if(segment == released) {
                        region.pseudoClassStateChanged(RELEASED_CLASS, true);
                        region.setTooltip(new Tooltip("Released"));
                    } else {
                        region.pseudoClassStateChanged(NOT_AIRED_CLASS, true);
                        region.setTooltip(new Tooltip("Not yet aired"));
                    }

                    region.setOnMouseClicked(e -> {
                        getTableView().getSelectionModel().select(getTableRow().getIndex());
                        getTableView().requestFocus();
                    });
                    return region;
                });
                statusBar.setOrientation(Orientation.HORIZONTAL);
                statusBar.setInfoNodeFactory(s -> null);

                var buttons = new BorderPane();
                buttons.setLeft(createButton("-", -1));
                buttons.setRight(createButton("+", 1));
                buttons.prefWidthProperty().bind(statusBar.widthProperty());
                buttons.prefHeightProperty().bind(statusBar.heightProperty());
                buttons.layoutXProperty().bind(statusBar.layoutXProperty());
                buttons.layoutYProperty().bind(statusBar.layoutYProperty());
                buttons.setPickOnBounds(false);
                var group = new Group(statusBar, buttons);
                graphic.getChildren().addAll(group, label = new Label());
            }

            private Button createButton(String text, int episodeIncrement) {
                var button = new JFXButton(text);
                button.setPadding(new Insets(-100));
                button.setPrefSize(14, 14);
                button.setTextAlignment(TextAlignment.CENTER);
                button.setTextFill(Color.WHITE);
                Runnable updateButton = () -> {
                    var userAnime = getTableRow().getItem();
                    if(userAnime == null) {
                        return;
                    }
                    var watched = userAnime.getEpisodeWatched();
                    var newWatched = watched + episodeIncrement;
                    var totalEpisodes = userAnime.getAnime().episodes();

                    var validEpsCount = newWatched >= 0
                            && (newWatched <= totalEpisodes || totalEpisodes == 0);
                    button.setOpacity(validEpsCount? 1.0 : 0.0);
                };
                button.setOnAction(evt -> {
                    var userAnime = getTableRow().getItem();
                    if(userAnime == null || button.getOpacity() == 0.0) {
                        return;
                    }
                    var newWatched = userAnime.getEpisodeWatched() + episodeIncrement;
                    var episodes = userAnime.getAnime().episodes();
                    var maxEpisodes = episodes == 0? Integer.MAX_VALUE : episodes;
                    newWatched = Math.max(Math.min(newWatched, maxEpisodes), 0);
                    userAnime.setEpisodeWatched(newWatched);
                    updateButton.run();
                });
                button.setOnMouseEntered(evt -> updateButton.run());
                button.getStyleClass().add("user-status-buttons");
                return button;
            }

            @Override
            protected void updateItem(UserAnime item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null && !empty) {

                    int totalEps = item.getAnime().episodes();
                    int releasedEps = Math.min(totalEps, 3);
                    IntegerProperty watchedEps = item.episodeWatchedProperty();
                    if(totalEps == 0) {
                        // binding version of watched.setValue(watchedEps > 0? 1.0 : 0.0)
                        watched.valueProperty().unbind();
                        watched.valueProperty().bind(Bindings.when(watchedEps.greaterThan(0))
                                .then(1.0).otherwise(0.0));

                        released.valueProperty().unbind();
                        released.setValue(0.0);

                        // binding version of notAired.setValue(watchedEps > 0? 0.0 : 1.0)
                        notAired.valueProperty().unbind();
                        notAired.valueProperty().bind(watched.valueProperty().negate().add(1.0));
                    } else {
                        // max(0, relesedEps - watchedEps)
                        var releasedBinding = Bindings.max(0, watchedEps.negate().add(releasedEps));

                        // totalEps - max(watchedEps, releasedEps)
                        var notAiredBinding = Bindings.max(watchedEps, releasedEps).negate().add(totalEps);
                        watched.valueProperty().unbind();
                        released.valueProperty().unbind();
                        notAired.valueProperty().unbind();

                        watched.valueProperty().bind(watchedEps.divide((double) totalEps));
                        released.valueProperty().bind(releasedBinding.divide((double) totalEps));
                        notAired.valueProperty().bind(notAiredBinding.divide((double) totalEps));
                    }
                    label.textProperty().unbind();
                    label.textProperty().bind(watchedEps.asString().concat("/")
                            .concat(totalEps == 0? "?" : Integer.toString(totalEps)));
                    setGraphic(graphic);
                } else {
                    setGraphic(null);
                }
            }
        });

        pane.getChildren().add(table);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setTopAnchor(table, 0.0);
    }

    private void addAnimeToList(UserAnime anime) {
        animeList.animes().add(anime);
        filteredAnimeLists.get(anime.getStatus()).add(anime);
    }

    public void windowClosed(Stage window) {
        var savePath = Path.of("animelist.json");
        while(true) {
            try {
                UserAnimeList.write(animeList, savePath);
                return;
            } catch (Exception e) {
                var dialog = new ExceptionDialog(e);
                var ignore = new ButtonType("Ignore and exit", ButtonBar.ButtonData.CANCEL_CLOSE);
                var saveToNewFile = new ButtonType("Save to new file", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().setAll(ignore, saveToNewFile);

                var button = dialog.showAndWait();
                if(button.isPresent() && button.get() == saveToNewFile) {
                    var fileChooser = new FileChooser();
                    fileChooser.setInitialFileName("animelist.json");
                    fileChooser.getExtensionFilters().setAll(new FileChooser.ExtensionFilter(
                            "JSON file", "*.json"
                    ));
                    var file = fileChooser.showSaveDialog(window);
                    if(file != null) {
                        savePath = file.toPath();
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }
}
