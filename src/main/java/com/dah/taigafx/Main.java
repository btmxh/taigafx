package com.dah.taigafx;

import com.dah.taigafx.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        var loader = new FXMLLoader(Objects.requireNonNull(Main.class.getResource("/mainWindow/MainWindow.fxml")));
        Parent parent = loader.load();
        MainWindowController controller = loader.getController();
        controller.setApplicationInstance(this);

        var scene = new Scene(parent);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/common/common.css")).toExternalForm());
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/mainWindow/animelist.css")).toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.setTitle("TaigaFX v0.1.0");
        primaryStage.setOnCloseRequest(evt -> controller.windowClosed());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
