package com.dah.taigafx.anime;

import org.jetbrains.annotations.NotNull;

public enum IdMappingMethod {
    ASK_USER("Ask me"),
    SEARCH("Search the title of the anime"),
    ARM_SERVER("Use BeeeQueue/arm-server's API (relations.yuna.moe)"),
    ANIME_OFFLINE_DATABASE("Use anime-offline-database (requires aod-extras)");

    private @NotNull final String displayString;

    IdMappingMethod(@NotNull String displayString) {
        this.displayString = displayString;
    }

    public String toString() {
        return displayString;
    }
}
