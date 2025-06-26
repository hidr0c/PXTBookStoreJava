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

public class ReportsUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Reports");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button generateReportButton = new Button("Generate New Report");
        generateReportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        generateReportButton.setOnAction(event -> generateNewReport());

        topSection.getChildren().addAll(titleLabel, generateReportButton);
        root.setTop(topSection);

        // Sales Reports table
        TableView<Object> salesReportsTable = new TableView<>();
        salesReportsTable.setPrefHeight(200);

        TableColumn<Object, String> reportIdColumn = new TableColumn<>("Report ID");
        TableColumn<Object, String> reportNameColumn = new TableColumn<>("Report Name");
        TableColumn<Object, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Object, String> totalSalesColumn = new TableColumn<>("Total Sales");
        TableColumn<Object, String> profitColumn = new TableColumn<>("Profit");

        salesReportsTable.getColumns().add(reportIdColumn);
        salesReportsTable.getColumns().add(reportNameColumn);
        salesReportsTable.getColumns().add(dateColumn);
        salesReportsTable.getColumns().add(totalSalesColumn);
        salesReportsTable.getColumns().add(profitColumn);

        // Inventory Reports table
        TableView<Object> inventoryReportsTable = new TableView<>();
        inventoryReportsTable.setPrefHeight(200);

        TableColumn<Object, String> bookIdColumn = new TableColumn<>("Book ID");
        TableColumn<Object, String> titleColumn = new TableColumn<>("Title");
        TableColumn<Object, String> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Object, String> valueColumn = new TableColumn<>("Value");
        TableColumn<Object, String> statusColumn = new TableColumn<>("Status");

        inventoryReportsTable.getColumns().add(bookIdColumn);
        inventoryReportsTable.getColumns().add(titleColumn);
        inventoryReportsTable.getColumns().add(quantityColumn);
        inventoryReportsTable.getColumns().add(valueColumn);
        inventoryReportsTable.getColumns().add(statusColumn);

        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));

        Label salesReportLabel = new Label("Sales Reports:");
        salesReportLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label inventoryReportLabel = new Label("Inventory Reports:");
        inventoryReportLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tablesSection.getChildren().addAll(
                salesReportLabel, salesReportsTable,
                inventoryReportLabel, inventoryReportsTable);

        root.setCenter(tablesSection);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Reports");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateNewReport() {
        Stage newWindow = new Stage();
        VBox newContent = new VBox(10);
        newContent.setPadding(new Insets(20));
        newContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label newWindowText = new Label("Generate New Report");
        newWindowText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        newContent.getChildren().add(newWindowText);

        Scene newScene = new Scene(newContent, 400, 300);
        newWindow.setTitle("Generate New Report");
        newWindow.setScene(newScene);
        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
