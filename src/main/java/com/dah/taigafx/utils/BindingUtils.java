package com.dah.taigafx.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

import java.util.function.Consumer;

public class BindingUtils {
    public static <T, R> ObjectBinding<R> map(ObservableValue<T> value, Callback<T, R> callback) {
        return Bindings.createObjectBinding(() -> callback.call(value.getValue()), value);
    }

    public static <T extends ObservableBooleanValue> void ifTrue(T value, Consumer<T> callback) {
        value.addListener((obs, old, newValue) -> {
            if(newValue) {
                callback.accept(value);
            }
        });
    }
}
