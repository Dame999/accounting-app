package com.accounting.app;

import com.accounting.app.database.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    private static Stage primaryStage;
    private static Scene currentScene;

    public static void changeScene(String fxmFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmFileName));
        Parent root = loader.load();
        currentScene = new Scene(root, 914, 662);
        primaryStage.setScene(currentScene);
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        changeScene("login-view.fxml");
        primaryStage.setTitle("Accounting Manager");
        primaryStage.show();

        DatabaseUtil.initializeDatabase();
    }
}