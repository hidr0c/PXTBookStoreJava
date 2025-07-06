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
        Label titleLabel = new Label("Báo cáo");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button generateReportButton = new Button("Tạo báo cáo mới");
        generateReportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        generateReportButton.setOnAction(event -> generateNewReport());

        topSection.getChildren().addAll(titleLabel, generateReportButton);
        root.setTop(topSection);

        // Sales Reports table
        TableView<Object> salesReportsTable = new TableView<>();
        salesReportsTable.setPrefHeight(200);

        TableColumn<Object, String> reportIdColumn = new TableColumn<>("Mã báo cáo");
        TableColumn<Object, String> reportNameColumn = new TableColumn<>("Tên báo cáo");
        TableColumn<Object, String> dateColumn = new TableColumn<>("Ngày");
        TableColumn<Object, String> totalSalesColumn = new TableColumn<>("Tổng doanh thu");
        TableColumn<Object, String> profitColumn = new TableColumn<>("Lợi nhuận");

        salesReportsTable.getColumns().add(reportIdColumn);
        salesReportsTable.getColumns().add(reportNameColumn);
        salesReportsTable.getColumns().add(dateColumn);
        salesReportsTable.getColumns().add(totalSalesColumn);
        salesReportsTable.getColumns().add(profitColumn);

        // Inventory Reports table
        TableView<Object> inventoryReportsTable = new TableView<>();
        inventoryReportsTable.setPrefHeight(200);
        TableColumn<Object, String> bookIdColumn = new TableColumn<>("Mã sách");
        TableColumn<Object, String> titleColumn = new TableColumn<>("Tiêu đề");
        TableColumn<Object, String> quantityColumn = new TableColumn<>("Số lượng");
        TableColumn<Object, String> valueColumn = new TableColumn<>("Giá trị");
        TableColumn<Object, String> statusColumn = new TableColumn<>("Trạng thái");

        inventoryReportsTable.getColumns().add(bookIdColumn);
        inventoryReportsTable.getColumns().add(titleColumn);
        inventoryReportsTable.getColumns().add(quantityColumn);
        inventoryReportsTable.getColumns().add(valueColumn);
        inventoryReportsTable.getColumns().add(statusColumn);

        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));

        Label salesReportLabel = new Label("Báo cáo doanh thu:");
        salesReportLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label inventoryReportLabel = new Label("Báo cáo kho hàng:");
        inventoryReportLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tablesSection.getChildren().addAll(
                salesReportLabel, salesReportsTable,
                inventoryReportLabel, inventoryReportsTable);

        root.setCenter(tablesSection);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Quản lý hiệu sách - Báo cáo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateNewReport() {
        Stage newWindow = new Stage();
        VBox newContent = new VBox(10);
        newContent.setPadding(new Insets(20));
        newContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label newWindowText = new Label("Tạo báo cáo mới");
        newWindowText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        newContent.getChildren().add(newWindowText);

        Scene newScene = new Scene(newContent, 400, 300);
        newWindow.setTitle("Tạo báo cáo mới");
        newWindow.setScene(newScene);
        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
