package com.dah.taigafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/MainWindow.fxml")));

        var scene = new Scene(parent);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/MainWindow.css")).toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.setTitle("TaigaFX v0.1.0");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
