package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StorageUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Storage Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button createNewButton = new Button("Create New Storage");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewStorageWindow());

        topSection.getChildren().addAll(titleLabel, createNewButton);
        root.setTop(topSection);

        // Storage table (related to OrderDetails)
        TableView<Object> storageTable = new TableView<>();
        storageTable.setPrefHeight(200);

        TableColumn<Object, String> orderIdColumn = new TableColumn<>("Order ID");
        TableColumn<Object, String> bookIdColumn = new TableColumn<>("Book ID");
        TableColumn<Object, String> unitPriceColumn = new TableColumn<>("Unit Price");
        TableColumn<Object, String> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

        storageTable.getColumns().add(orderIdColumn);
        storageTable.getColumns().add(bookIdColumn);
        storageTable.getColumns().add(unitPriceColumn);
        storageTable.getColumns().add(quantityColumn);
        storageTable.getColumns().add(methodTypeColumn);

        // Orders table (related to Storage)
        TableView<Object> ordersTable = new TableView<>();
        ordersTable.setPrefHeight(200);

        TableColumn<Object, String> orderIdOrderColumn = new TableColumn<>("Order ID");
        TableColumn<Object, String> orderDateColumn = new TableColumn<>("Order Date");
        TableColumn<Object, String> totalAmountColumn = new TableColumn<>("Total Amount");
        TableColumn<Object, String> statusColumn = new TableColumn<>("Status");
        TableColumn<Object, String> customerIdColumn = new TableColumn<>("Customer ID");
        TableColumn<Object, String> methodTypeOrderColumn = new TableColumn<>("Method Type");

        ordersTable.getColumns().add(orderIdOrderColumn);
        ordersTable.getColumns().add(orderDateColumn);
        ordersTable.getColumns().add(totalAmountColumn);
        ordersTable.getColumns().add(statusColumn);
        ordersTable.getColumns().add(customerIdColumn);
        ordersTable.getColumns().add(methodTypeOrderColumn);

        // Add tables to layout
        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));

        Label storageLabel = new Label("Storage Details:");
        storageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label orderLabel = new Label("Order Details:");
        orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tablesSection.getChildren().addAll(
                storageLabel, storageTable,
                orderLabel, ordersTable);

        root.setCenter(tablesSection);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Storage Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateNewStorageWindow() {
        Stage newWindow = new Stage();
        VBox newContent = new VBox(10);
        newContent.setPadding(new Insets(20));
        newContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label newWindowText = new Label("Create New Storage");
        newWindowText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        newContent.getChildren().add(newWindowText);

        Scene newScene = new Scene(newContent, 400, 300);
        newWindow.setTitle("Create New Storage");
        newWindow.setScene(newScene);
        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}