package org.example;

import javafx.application.Application;
import javafx.stage.Stage;

public class BookStoreUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginUI loginUI = new LoginUI();
        loginUI.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


