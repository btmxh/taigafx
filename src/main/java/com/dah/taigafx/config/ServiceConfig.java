package com.dah.taigafx.config;

import com.dah.taigafx.data.anime.AnimeSource;
import com.dah.taigafx.data.anime.IdMappingMethod;
import com.dah.taigafx.data.animelist.AnimeListService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class ServiceConfig {
    private @NotNull final SimpleObjectProperty<@NotNull AnimeSource> metadataProvider
            = new SimpleObjectProperty<>(AnimeSource.ANILIST);
    private @NotNull final SimpleObjectProperty<@Nullable AnimeListService> animeListService
            = new SimpleObjectProperty<>(AnimeListService.ANILIST);
    private @NotNull final SimpleObjectProperty<@Nullable Path> aodExtrasDirectory
            = new SimpleObjectProperty<>(null);
    private @NotNull final SimpleObjectProperty<@NotNull IdMappingMethod> idMappingMethod
            = new SimpleObjectProperty<>(IdMappingMethod.SEARCH);
    private @NotNull final SimpleBooleanProperty
            autoSyncWhenListChange = new SimpleBooleanProperty(false),
            autoSyncOnStartup = new SimpleBooleanProperty(false),
            autoSyncOnExit = new SimpleBooleanProperty(false),
            autoSyncPeriodically = new SimpleBooleanProperty(false);
    private @NotNull final SimpleObjectProperty<@Nullable Duration>
            autoSyncPeriod = new SimpleObjectProperty<>();
    private @NotNull ObservableList<@NotNull Account>
            accounts = FXCollections.observableArrayList();

    public @NotNull AnimeSource getMetadataProvider() {
        return metadataProvider.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull AnimeSource> metadataProviderProperty() {
        return metadataProvider;
    }

    public void setMetadataProvider(@NotNull AnimeSource metadataProvider) {
        this.metadataProvider.set(metadataProvider);
    }

    public @Nullable AnimeListService getAnimeListService() {
        return animeListService.get();
    }

    public @NotNull SimpleObjectProperty<@Nullable AnimeListService> animeListServiceProperty() {
        return animeListService;
    }

    public void setAnimeListService(@Nullable AnimeListService animeListService) {
        this.animeListService.set(animeListService);
    }

    public @Nullable Path getAodExtrasDirectory() {
        return aodExtrasDirectory.get();
    }

    public @NotNull SimpleObjectProperty<@Nullable Path> aodExtrasDirectoryProperty() {
        return aodExtrasDirectory;
    }

    public void setAodExtrasDirectory(@Nullable Path aodExtrasDirectory) {
        this.aodExtrasDirectory.set(aodExtrasDirectory);
    }

    public @NotNull IdMappingMethod getIdMappingMethod() {
        return idMappingMethod.get();
    }

    public @NotNull SimpleObjectProperty<@NotNull IdMappingMethod> idMappingMethodProperty() {
        return idMappingMethod;
    }

    public void setIdMappingMethod(@NotNull IdMappingMethod idMappingMethod) {
        this.idMappingMethod.set(idMappingMethod);
    }

    public boolean isAutoSyncWhenListChange() {
        return autoSyncWhenListChange.get();
    }

    public @NotNull SimpleBooleanProperty autoSyncWhenListChangeProperty() {
        return autoSyncWhenListChange;
    }

    public void setAutoSyncWhenListChange(boolean autoSyncWhenListChange) {
        this.autoSyncWhenListChange.set(autoSyncWhenListChange);
    }

    public boolean isAutoSyncOnStartup() {
        return autoSyncOnStartup.get();
    }

    public @NotNull SimpleBooleanProperty autoSyncOnStartupProperty() {
        return autoSyncOnStartup;
    }

    public void setAutoSyncOnStartup(boolean autoSyncOnStartup) {
        this.autoSyncOnStartup.set(autoSyncOnStartup);
    }

    public boolean isAutoSyncOnExit() {
        return autoSyncOnExit.get();
    }

    public @NotNull SimpleBooleanProperty autoSyncOnExitProperty() {
        return autoSyncOnExit;
    }

    public void setAutoSyncOnExit(boolean autoSyncOnExit) {
        this.autoSyncOnExit.set(autoSyncOnExit);
    }

    public boolean isAutoSyncPeriodically() {
        return autoSyncPeriodically.get();
    }

    public @NotNull SimpleBooleanProperty autoSyncPeriodicallyProperty() {
        return autoSyncPeriodically;
    }

    public void setAutoSyncPeriodically(boolean autoSyncPeriodically) {
        this.autoSyncPeriodically.set(autoSyncPeriodically);
    }

    public @Nullable Duration getAutoSyncPeriod() {
        return autoSyncPeriod.get();
    }

    public @NotNull SimpleObjectProperty<@Nullable Duration> autoSyncPeriodProperty() {
        return autoSyncPeriod;
    }

    public void setAutoSyncPeriod(@Nullable Duration autoSyncPeriod) {
        this.autoSyncPeriod.set(autoSyncPeriod);
    }

    public @NotNull ObservableList<Account> getAccounts() {
        return accounts;
    }

    // Jackson only
    public void setAccounts(List<Account> accounts) {
        this.accounts = FXCollections.observableArrayList(accounts);
    }

    public static class Account {}
}
