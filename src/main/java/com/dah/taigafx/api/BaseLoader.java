package com.dah.taigafx.api;

import com.dah.taigafx.Provider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class BaseLoader {
    protected @NotNull final Provider provider;

    public BaseLoader(@NotNull Provider provider) {
        this.provider = provider;
    }

    public @NotNull HttpClient getHttpClient() {
        return provider.getHttpClient();
    }

    public @NotNull ObjectMapper getObjectMapper() {
        return provider.getObjectMapper();
    }

    public @NotNull Duration getTimeout() {
        return provider.getConnectionTimeout();
    }

    public HttpRequest.Builder buildRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url)).timeout(getTimeout());
    }
}
