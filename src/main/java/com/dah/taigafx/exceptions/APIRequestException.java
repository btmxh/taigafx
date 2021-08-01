package com.dah.taigafx.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class APIRequestException extends Exception {
    private @NotNull final List<Error> errors;

    public APIRequestException(@NotNull List<Error> errors) {
        super(generateErrorMessage(errors));
        this.errors = errors;
    }

    private static String generateErrorMessage(List<Error> errors) {
        if(errors.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return errors.size() + " errors from API: " + errors;
    }

    public @NotNull List<Error> getErrors() {
        return errors;
    }



    public static record Error(int errorCode, String message) {}
}
