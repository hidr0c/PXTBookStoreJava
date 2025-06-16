package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReportsUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Reports");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        root.getChildren().add(titleLabel);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Reports");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
