package com.dah.taigafx.controllers;

import com.dah.taigafx.data.anime.AnimeSource;
import com.dah.taigafx.data.anime.IdMappingMethod;
import com.dah.taigafx.data.animelist.AnimeListService;
import com.dah.taigafx.config.AnimeListConfig;
import com.dah.taigafx.config.Config;
import com.dah.taigafx.utils.BindingUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

public class ConfigWindowController {
    private MainWindowController mainController;

    @FXML
    private ScrollPane animeListPane;
    @FXML
    private ChoiceBox<AnimeListConfig.AnimeListAction> doubleClickAction;
    @FXML
    private ChoiceBox<AnimeListConfig.AnimeListAction> middleClickAction;
    @FXML
    private ChoiceBox<AnimeListConfig.AnimeListAction> ctrlClickAction;
    @FXML
    private ChoiceBox<AnimeListConfig.AnimeListAction> altClickAction;
    @FXML
    private CheckBox highlightAnimeNewEps;
    @FXML
    private CheckBox highlightAnimeNewDownloadEps;
    @FXML
    private ScrollPane servicesPane;
    @FXML
    private ChoiceBox<AnimeSource> metadataProvider;
    @FXML
    private ChoiceBox<AnimeListService> animeListService;
    @FXML
    private TextField aodExtrasDir;
    @FXML
    private ChoiceBox<IdMappingMethod> idMappingMethod;
    @FXML
    private CheckBox syncWhenChange;
    @FXML
    private CheckBox syncStartup;
    @FXML
    private CheckBox syncExit;
    @FXML
    private CheckBox syncPeriodically;
    @FXML
    private Spinner<Integer> syncPeriodAmount;
    @FXML
    private ChoiceBox<ChronoUnit> syncPeriodUnit;
    @FXML
    private TreeView<ConfigSection> categories;

    public void initialize() {
        var root = new TreeItem<ConfigSection>();
        root.getChildren().addAll(ConfigSection.getItems());
        categories.setRoot(root);
        
        categories.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newValue) -> bringSettingsOnTop(newValue.getValue()));

        metadataProvider.getItems().setAll(AnimeSource.values());
        animeListService.getItems().setAll(AnimeListService.values());
        idMappingMethod.getItems().setAll(IdMappingMethod.values());

        syncPeriodAmount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, Integer.MAX_VALUE, 1, 1));
        syncPeriodAmount.getValueFactory().setConverter(new IntegerStringConverter());
        syncPeriodAmount.disableProperty().bind(syncPeriodically.selectedProperty().not());
        syncPeriodUnit.getItems().setAll(ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS);
        syncPeriodUnit.disableProperty().bind(syncPeriodically.selectedProperty().not());

        var notUsingAod =
                metadataProvider.valueProperty().isNotEqualTo(AnimeSource.ANIME_OFFLINE_DATABASE)
                .and(idMappingMethod.valueProperty().isNotEqualTo(IdMappingMethod.ANIME_OFFLINE_DATABASE));
        aodExtrasDir.getParent().disableProperty().bind(notUsingAod);

        var notUseALService = animeListService.getSelectionModel().selectedItemProperty()
                .isEqualTo(AnimeListService.USE_NONE);
        for(var sync : List.of(syncWhenChange, syncStartup, syncExit, syncPeriodically)) {
            sync.disableProperty().bind(notUseALService);
        }
        BindingUtils.ifTrue(notUseALService, ignore -> {
            for(var sync : List.of(syncWhenChange, syncStartup, syncExit, syncPeriodically)) {
                sync.setSelected(false);
            }
        });

        for(var action : List.of(doubleClickAction, middleClickAction, ctrlClickAction, altClickAction)) {
            action.getItems().setAll(AnimeListConfig.AnimeListAction.values());
        }

        categories.getSelectionModel().select(0);
    }

    private void bringSettingsOnTop(ConfigSection section) {
        switch(section) {
            case SERVICES -> servicesPane.toFront();
            case ANIME_LIST -> animeListPane.toFront();
        }
    }

    public void setMainController(MainWindowController controller) {
        this.mainController = controller;
        syncConfig(controller.getConfig());
    }

    private void syncConfig(Config config) {
        // SERVICE
        var service = config.service();
        // Setting initial values from the config
        metadataProvider.getSelectionModel().select(service.getMetadataProvider());
        animeListService.getSelectionModel().select(service.getAnimeListService());

        var aodExtraPath = service.getAodExtrasDirectory();
        aodExtrasDir.setText(aodExtraPath == null? "" : aodExtraPath.toAbsolutePath().toString());

        var syncPeriod = service.getAutoSyncPeriod();
        if(syncPeriod != null && !syncPeriod.isNegative() && !syncPeriod.isZero()) {
            // will ignore nanoseconds even if the user edits the config file
            var seconds = syncPeriod.getSeconds();
            if(seconds % 3600 == 0) {
                syncPeriodUnit.getSelectionModel().select(ChronoUnit.HOURS);
                syncPeriodAmount.getValueFactory().setValue((int) (seconds / 3600));
            } else if(seconds % 60 == 0) {
                syncPeriodUnit.getSelectionModel().select(ChronoUnit.MINUTES);
                syncPeriodAmount.getValueFactory().setValue((int) (seconds / 60));
            } else {
                syncPeriodUnit.getSelectionModel().select(ChronoUnit.SECONDS);
                syncPeriodAmount.getValueFactory().setValue((int) (seconds));
            }
        } else {
            syncPeriodUnit.getSelectionModel().select(ChronoUnit.HOURS);
            syncPeriodAmount.getValueFactory().setValue(0);
        }

        idMappingMethod.getSelectionModel().select(service.getIdMappingMethod());

        syncWhenChange.setSelected(service.isAutoSyncWhenListChange());
        syncStartup.setSelected(service.isAutoSyncOnStartup());
        syncExit.setSelected(service.isAutoSyncOnExit());
        syncPeriodically.setSelected(service.isAutoSyncPeriodically());

        var animeList = config.animeList();
        doubleClickAction.setValue(animeList.getDoubleClick());
        middleClickAction.setValue(animeList.getMiddleClick());
        ctrlClickAction.setValue(animeList.getCtrlClick());
        altClickAction.setValue(animeList.getAltClick());
        highlightAnimeNewEps.setSelected(animeList.isHighlightAnimeNewEps());
        highlightAnimeNewDownloadEps.setSelected(animeList.isHighlightAnimeNewDownloadEps());
    }

    public void importAnimeList(ActionEvent evt) {
    }

    public void exportAnimeList(ActionEvent evt) {
    }

    public void browseAodExtrasDir() {
        var window = getWindow();
        var chooser = new DirectoryChooser();
        chooser.setTitle("Browse aod-extras directory");
        var dir = chooser.showDialog(window);
        aodExtrasDir.setText(dir.getAbsolutePath());
    }

    public void openManageAccounts() {
    }

    public void closeWindow() {
        ((Stage) getWindow()).close();
    }

    public void showHelp() {
        mainController.getApplicationInstance().getHostServices()
                .showDocument("https://www.youtube.com/watch?v=V-_O7nl0Ii0");
    }

    public void saveConfig() {
        var config = mainController.getConfig();
        var service = config.service();
        service.setMetadataProvider(metadataProvider.getSelectionModel().getSelectedItem());
        service.setAnimeListService(animeListService.getSelectionModel().getSelectedItem());
        service.setIdMappingMethod(idMappingMethod.getSelectionModel().getSelectedItem());
        service.setAutoSyncPeriod(Duration.of(syncPeriodAmount.getValue(), syncPeriodUnit.getValue()));
        service.setAutoSyncWhenListChange(syncWhenChange.isSelected());
        service.setAutoSyncOnStartup(syncStartup.isSelected());
        service.setAutoSyncOnExit(syncExit.isSelected());
        service.setAutoSyncPeriodically(syncPeriodically.isSelected());

        var animeList = config.animeList();
        animeList.setDoubleClick(doubleClickAction.getValue());
        animeList.setMiddleClick(middleClickAction.getValue());
        animeList.setCtrlClick(ctrlClickAction.getValue());
        animeList.setAltClick(altClickAction.getValue());
        animeList.setHighlightAnimeNewEps(highlightAnimeNewEps.isSelected());
        animeList.setHighlightAnimeNewDownloadEps(highlightAnimeNewDownloadEps.isSelected());

        closeWindow();
    }

    // must not be called in initialize()
    private Window getWindow() {
        return categories.getScene().getWindow();
    }

    private enum ConfigSection {
        SERVICES("Services"),
        ANIME_LIST("Anime List");
        private @NotNull final String displayString;
        private @NotNull final ConfigSection[] children;
        private boolean root = true;

        ConfigSection(@NotNull String displayString, ConfigSection... children) {
            this.displayString = displayString;
            this.children = children;
            Arrays.stream(children).forEach(s -> s.root = false);
        }

        @Override
        public String toString() {
            return displayString;
        }

        private TreeItem<ConfigSection> createItem() {
            var item = new TreeItem<>(this);
            Arrays.stream(children).map(ConfigSection::createItem)
                    .forEach(item.getChildren()::add);
            return item;
        }

        public static List<TreeItem<ConfigSection>> getItems() {
            return Arrays.stream(values()).filter(s -> s.root)
                    .map(ConfigSection::createItem)
                    .toList();
        }
    }
}
