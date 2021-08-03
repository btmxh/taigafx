package com.dah.taigafx.anime.loaders;

import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.time.Duration;

public class BaseLoader {
    protected @NotNull final HttpClient httpClient;
    protected @NotNull Duration timeout;

    public BaseLoader(@NotNull HttpClient httpClient, @NotNull Duration timeout) {
        this.httpClient = httpClient;
        this.timeout = timeout;
    }

    public BaseLoader(@NotNull Duration timeout) {
        this(HttpClient.newBuilder().build(), timeout);
    }

    public BaseLoader() {
        this(Duration.ofMinutes(2));
    }

    public void setTimeout(@NotNull Duration timeout) {
        this.timeout = timeout;
    }
}
