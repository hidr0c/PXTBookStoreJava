package org.example;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StaffUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Staff Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button createNewButton = new Button("Create New Staff");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction((ActionEvent e) -> showCreateNewStaffWindow());

        topSection.getChildren().addAll(titleLabel, createNewButton);
        root.setTop(topSection);

        // Staff table
        TableView<Object> staffTable = new TableView<>();
        staffTable.setPrefHeight(300);

        TableColumn<Object, String> staffIdColumn = new TableColumn<>("Staff ID");
        TableColumn<Object, String> positionColumn = new TableColumn<>("Position");
        TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

        staffTable.getColumns().addAll(staffIdColumn, positionColumn, methodTypeColumn);

        // User table (related to Staff)
        TableView<Object> userTable = new TableView<>();
        userTable.setPrefHeight(300);

        TableColumn<Object, String> userIdColumn = new TableColumn<>("User ID");
        TableColumn<Object, String> fullNameColumn = new TableColumn<>("Full Name");
        TableColumn<Object, String> addressColumn = new TableColumn<>("Address");
        TableColumn<Object, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        TableColumn<Object, String> methodTypeUserColumn = new TableColumn<>("Method Type");

        userTable.getColumns().addAll(userIdColumn, fullNameColumn, addressColumn, phoneNumberColumn,
                methodTypeUserColumn);

        // Add tables to layout
        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));

        Label staffLabel = new Label("Staff Details:");
        staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label userLabel = new Label("User Details:");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tablesSection.getChildren().addAll(
                staffLabel, staffTable,
                userLabel, userTable);

        root.setCenter(tablesSection);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Staff Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateNewStaffWindow() {
        Stage newWindow = new Stage();
        VBox newContent = new VBox(10);
        newContent.setPadding(new Insets(20));
        newContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label newWindowText = new Label("Create New Staff");
        newWindowText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        newContent.getChildren().add(newWindowText);

        Scene newScene = new Scene(newContent, 400, 300);
        newWindow.setTitle("Create New Staff");
        newWindow.setScene(newScene);
        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}