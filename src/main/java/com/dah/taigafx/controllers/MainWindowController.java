package com.dah.taigafx.controllers;

import com.dah.taigafx.Main;
import com.dah.taigafx.Provider;
import com.dah.taigafx.data.anime.Anime;
import com.dah.taigafx.data.anime.AnimeSeason;
import com.dah.taigafx.data.anime.AnimeStatus;
import com.dah.taigafx.data.anime.AnimeType;
import com.dah.taigafx.data.animelist.UserAnime;
import com.dah.taigafx.data.animelist.UserAnimeList;
import com.dah.taigafx.data.animelist.UserAnimeStatus;
import com.dah.taigafx.config.Config;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import org.controlsfx.control.SegmentedBar;
import org.controlsfx.dialog.ExceptionDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class MainWindowController {
    public AnchorPane animeListPane;
    public AnchorPane allAnimePane;
    public AnchorPane currentlyWatchingPane;
    public AnchorPane completedPane;
    public AnchorPane onHoldPane;
    public AnchorPane droppedPane;
    public AnchorPane ptwPane;

    public AnchorPane searchPane;
    public TextField animeSearchQueryTextField;
    public TableView<Anime> searchTable;
    public TableColumn<Anime, AnimeStatus> searchTableAnimeStatusCol;
    public TableColumn<Anime, String> searchTableAnimeTitleCol;
    public TableColumn<Anime, AnimeType> searchTableAnimeTypeCol;
    public TableColumn<Anime, Integer> searchTableAnimeEpisodesCol;
    public TableColumn<Anime, Double> searchTableAnimeScoreCol;
    public TableColumn<Anime, AnimeSeason.WithYear> searchTableAnimeSeasonCol;

    // Settings
    private ConfigWindowController configController;
    private AnchorPane configPane;
    private Stage configStage;

    private final EnumMap<UserAnimeStatus, ObservableList<UserAnime>> filteredAnimeLists
            = new EnumMap<>(UserAnimeStatus.class);
    private UserAnimeList animeList;
    private Config userConfig;
    private Main app;

    private Provider provider;

    @FXML
    public void initialize() {
        // TODO: Move these file to a dedicated directory for TaigaFX
        var configFile = Path.of("config.json");
        var animelistFile = Path.of("animelist.json");

        try {
            userConfig = Objects.requireNonNull(Config.read(new Provider(new Config()), configFile));
        } catch (IOException ex) {
            System.err.println("Failed to read config file, using default instead. Exception stack trace:");
            ex.printStackTrace();
            userConfig = new Config();
        }

        provider = new Provider(userConfig);

        try {
            animeList = Objects.requireNonNull(UserAnimeList.read(provider, animelistFile));
        } catch (IOException ex) {
            System.err.println("Failed to read anime list file, creating new instead. Exception stack trace:");
            ex.printStackTrace();
            animeList = new UserAnimeList("anon", FXCollections.observableArrayList());
        }

        for (final var status : UserAnimeStatus.values()) {
            filteredAnimeLists.put(status, FXCollections.observableArrayList());
        }

        for (final var anime : animeList.getAnimes()) {
            filteredAnimeLists.get(anime.getStatus()).add(anime);
        }

        initAnimePane(allAnimePane, null);
        initAnimePane(currentlyWatchingPane, UserAnimeStatus.WATCHING);
        initAnimePane(completedPane, UserAnimeStatus.COMPLETED);
        initAnimePane(onHoldPane, UserAnimeStatus.ON_HOLD);
        initAnimePane(droppedPane, UserAnimeStatus.DROPPED);
        initAnimePane(ptwPane, UserAnimeStatus.PLAN_TO_WATCH);

        searchTableAnimeStatusCol.setCellFactory(getAnimeStatusCellFactory());
        searchTableAnimeStatusCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().status()));
        searchTableAnimeTitleCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().title()));
        searchTableAnimeTypeCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().type()));
        searchTableAnimeEpisodesCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().episodes()));
        searchTableAnimeScoreCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().score()));
        searchTableAnimeSeasonCol.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(data.getValue().seasonWithYear()));
    }

    private void initAnimePane(@NotNull AnchorPane pane, @Nullable UserAnimeStatus status) {
        var list = status == null ? animeList.getAnimes() :
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

        animeStatusCol.setCellFactory(getAnimeStatusCellFactory());

        userStatusCol.setCellFactory(col -> new TableCell<>() {
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
                            return new SkinBase<Control>(this) {
                            };
                        }
                    };

                    region.getStyleClass().add("status-bar-segment");
                    if (segment == watched) {
                        region.pseudoClassStateChanged(WATCHED_CLASS, true);
                        region.setTooltip(new Tooltip("Watched"));
                    } else if (segment == released) {
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
                    if (userAnime == null) {
                        return;
                    }
                    var watched = userAnime.getEpisodeWatched();
                    var newWatched = watched + episodeIncrement;
                    var totalEpisodes = userAnime.getAnime().episodes();

                    var validEpsCount = newWatched >= 0
                            && (newWatched <= totalEpisodes || totalEpisodes == 0);
                    button.setOpacity(validEpsCount ? 1.0 : 0.0);
                };
                button.setOnAction(evt -> {
                    var userAnime = getTableRow().getItem();
                    if (userAnime == null || button.getOpacity() == 0.0) {
                        return;
                    }
                    var newWatched = userAnime.getEpisodeWatched() + episodeIncrement;
                    var episodes = userAnime.getAnime().episodes();
                    var maxEpisodes = episodes == 0 ? Integer.MAX_VALUE : episodes;
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
                if (item != null && !empty) {

                    int totalEps = item.getAnime().episodes();
                    int releasedEps = Math.min(totalEps, 3);
                    IntegerProperty watchedEps = item.episodeWatchedProperty();
                    if (totalEps == 0) {
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
                            .concat(totalEps == 0 ? "?" : Integer.toString(totalEps)));
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

    public void selectAnimeList() {
        animeListPane.toFront();
    }

    public void selectSearch() {
        searchPane.toFront();
    }

    private Task<Void> previousSearchTask;

    public void doSearch() {
        var query = animeSearchQueryTextField.getText();
        var itr = provider.getAnimeLoader().searchAnimeAsIterator(query);
        searchTable.getItems().clear();
        if(previousSearchTask != null && !previousSearchTask.isCancelled()) {
            previousSearchTask.cancel();
        }

        var searchTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (itr.hasNext()) {
                    if(isCancelled()) {
                        return null;
                    }
                    var result = itr.next().get();
                    if(result != null && !isCancelled()) {
                        Platform.runLater(() -> searchTable.getItems().addAll(result.animes()));
                    }
                }
                return null;
            }
        };

        previousSearchTask = searchTask;
        new Thread(searchTask).start();
    }

    public void openSettings(ActionEvent evt) {
        if (configStage == null) {
            try {
                var loader = new FXMLLoader(MainWindowController.class.getResource("/config/ConfigWindow.fxml"));
                var eventSource = (Node) evt.getSource();
                configPane = loader.load();
                configController = loader.getController();
                configController.setMainController(this);
                Scene configScene = new Scene(configPane);
                configScene.getStylesheets().add(Objects.requireNonNull(MainWindowController.class.getResource("/common/common.css")).toExternalForm());
                configStage = new Stage();
                configStage.initOwner(eventSource.getScene().getWindow());
                configStage.initModality(Modality.WINDOW_MODAL);
                configStage.setScene(configScene);
                configStage.setTitle("Settings");
            } catch (IOException e) {
                new ExceptionDialog(e).showAndWait();
                return;
            }
        }
        configStage.showAndWait();
    }

    public void windowClosed() {
        tryToSaveAnimeList(Path.of("animelist.json"));
        tryToSaveConfig(Path.of("config.json"));
    }

    public void tryToSaveAnimeList(Path animeListFile) {
        if(animeListFile == null)   return;
        try {
            UserAnimeList.write(provider, animeList, animeListFile);
        } catch (IOException ex) {
            if(showIOExceptionDialog(ex, "An exception occurred", "Error saving anime list file at '" + animeListFile.toAbsolutePath() + "'")) {
                var newAnimeListFile = saveFileChooserSimple(getWindow(),
                        animeListFile.getFileName().toString(), "JSON file",
                        "*.json");
                tryToSaveAnimeList(newAnimeListFile);
            }
        }
    }

    public void tryToSaveConfig(Path configFile) {
        if(configFile == null)  return;
        try {
            Config.write(provider, userConfig, configFile);
        } catch (IOException ex) {
            if(showIOExceptionDialog(ex, "An exception occurred", "Error saving config file at '" + configFile.toAbsolutePath() + "'")) {
                var newConfigFile = saveFileChooserSimple(getWindow(),
                        configFile.getFileName().toString(), "JSON file",
                        "*.json");
                tryToSaveConfig(newConfigFile);
            }
        }
    }

    // must not be called in initialize()
    private Window getWindow() {
        return allAnimePane.getScene().getWindow();
    }

    private boolean showIOExceptionDialog(IOException ex, String title, String header) {
        var dialog = new ExceptionDialog(ex);
        if(title != null)   dialog.setTitle(title);
        if(header != null)  dialog.setHeaderText(header);
        var ignore = new ButtonType("Ignore and exit", ButtonBar.ButtonData.CANCEL_CLOSE);
        var saveToNewFile = new ButtonType("Save to new file", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(ignore, saveToNewFile);
        return dialog.showAndWait().filter(t -> t == saveToNewFile).isPresent();
    }

    private Path saveFileChooserSimple(Window owner, String filename, String filterName, String... filterExts) {
        var chooser = new FileChooser();
        if(filename != null) chooser.setInitialFileName(filename);
        if(filterName != null && filterExts != null) {
            chooser.getExtensionFilters().setAll(
                    new FileChooser.ExtensionFilter(filterName, filterExts)
            );
        }
        var file = chooser.showSaveDialog(owner);
        return file == null? null : file.toPath();
    }

    public Config getConfig() {
        return userConfig;
    }

    public void setApplicationInstance(Main app) {
        this.app = app;
    }

    public Main getApplicationInstance() {
        return app;
    }

    private static <T> Callback<TableColumn<T, AnimeStatus>, TableCell<T, AnimeStatus>> getAnimeStatusCellFactory() {
        return ignore -> new TableCell<>() {
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
                rect.getStyleClass().add("anime-status-display");
            }

            @Override
            protected void updateItem(AnimeStatus item, boolean empty) {
                super.updateItem(item, empty);
                statusPseudoClasses.values().forEach(pseudoClass ->
                        rect.pseudoClassStateChanged(pseudoClass, false));
                if (item != null && !empty) {
                    setGraphic(rect);
                    rect.pseudoClassStateChanged(statusPseudoClasses.get(item), true);
                } else {
                    setGraphic(null);
                }
            }
        };
    }
}
