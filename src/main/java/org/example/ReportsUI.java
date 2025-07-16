package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class ReportsUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(javafx.geometry.Pos.CENTER);
        Label titleLabel = new Label("Báo cáo");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button generateReportButton = new Button("Tạo báo cáo mới");
        generateReportButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        topSection.getChildren().addAll(titleLabel, generateReportButton);
        root.setTop(topSection);
        TableView<Object> salesReportsTable = new TableView<>();
        salesReportsTable.setPrefHeight(200);
        TableColumn<Object, String> reportIdColumn = new TableColumn<>("Mã báo cáo");
        TableColumn<Object, String> reportNameColumn = new TableColumn<>("Tên báo cáo");
        TableColumn<Object, String> dateColumn = new TableColumn<>("Ngày");
        TableColumn<Object, String> totalSalesColumn = new TableColumn<>("Tổng doanh thu");
        TableColumn<Object, String> profitColumn = new TableColumn<>("Lợi nhuận");
        salesReportsTable.getColumns().addAll(reportIdColumn, reportNameColumn, dateColumn, totalSalesColumn,
                profitColumn);
        // Thêm nút Thêm cho bảng báo cáo doanh thu
        Button addSalesReportBtn = new Button("Thêm báo cáo doanh thu");
        addSalesReportBtn.setOnAction(e -> showAddSalesReportPopup());
        // Thêm icon sửa/xóa vào từng dòng bảng báo cáo doanh thu
        TableColumn<Object, Void> salesReportActionCol = new TableColumn<>("Thao tác");
        salesReportActionCol.setCellFactory(col -> new TableCell<Object, Void>() {
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            {
                editBtn.setGraphic(new Label("✏️"));
                editBtn.setStyle("-fx-background-color: transparent;");
                editBtn.setOnAction(ev -> showEditSalesReportPopup(getTableView().getItems().get(getIndex())));
                deleteBtn.setGraphic(new Label("🗑️"));
                deleteBtn.setStyle("-fx-background-color: transparent;");
                deleteBtn.setOnAction(ev -> showDeleteSalesReportConfirm(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
        salesReportsTable.getColumns().add(salesReportActionCol);
        TableView<Object> inventoryReportsTable = new TableView<>();
        inventoryReportsTable.setPrefHeight(200);
        TableColumn<Object, String> bookIdColumn = new TableColumn<>("Mã sách");
        TableColumn<Object, String> titleColumn = new TableColumn<>("Tiêu đề");
        TableColumn<Object, String> quantityColumn = new TableColumn<>("Số lượng");
        TableColumn<Object, String> valueColumn = new TableColumn<>("Giá trị");
        TableColumn<Object, String> statusColumn = new TableColumn<>("Trạng thái");
        inventoryReportsTable.getColumns().addAll(bookIdColumn, titleColumn, quantityColumn, valueColumn, statusColumn);
        // Thêm nút Thêm cho bảng báo cáo kho
        Button addInventoryReportBtn = new Button("Thêm báo cáo kho");
        addInventoryReportBtn.setOnAction(e -> showAddInventoryReportPopup());
        // Thêm icon sửa/xóa vào từng dòng bảng báo cáo kho
        TableColumn<Object, Void> inventoryReportActionCol = new TableColumn<>("Thao tác");
        inventoryReportActionCol.setCellFactory(col -> new TableCell<Object, Void>() {
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            {
                editBtn.setGraphic(new Label("✏️"));
                editBtn.setStyle("-fx-background-color: transparent;");
                editBtn.setOnAction(ev -> showEditInventoryReportPopup(getTableView().getItems().get(getIndex())));
                deleteBtn.setGraphic(new Label("🗑️"));
                deleteBtn.setStyle("-fx-background-color: transparent;");
                deleteBtn
                        .setOnAction(ev -> showDeleteInventoryReportConfirm(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
        inventoryReportsTable.getColumns().add(inventoryReportActionCol);
        // Sửa lỗi chưa khai báo salesReportBtnBox, inventoryReportBtnBox
        HBox salesReportBtnBox = new HBox(10);
        salesReportBtnBox.setPadding(new Insets(10));
        salesReportBtnBox.getChildren().add(addSalesReportBtn);
        HBox inventoryReportBtnBox = new HBox(10);
        inventoryReportBtnBox.setPadding(new Insets(10));
        inventoryReportBtnBox.getChildren().add(addInventoryReportBtn);

        // Sửa lại layout cho TableView và nút CRUD
        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));
        Label salesReportLabel = new Label("Báo cáo doanh thu:");
        salesReportLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label inventoryReportLabel = new Label("Báo cáo kho hàng:");
        inventoryReportLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        tablesSection.getChildren().addAll(
                salesReportLabel, salesReportBtnBox, salesReportsTable,
                inventoryReportLabel, inventoryReportBtnBox, inventoryReportsTable);
        root.setCenter(tablesSection);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Quản lý hiệu sách - Báo cáo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Popup thêm báo cáo doanh thu
    private void showAddSalesReportPopup() {
        Stage dialog = new Stage();
        dialog.setTitle("Thêm báo cáo doanh thu mới");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().add(new Label("Nhập thông tin báo cáo doanh thu mới"));
        // TODO: Thêm các TextField nhập liệu
        Button saveBtn = new Button("Lưu");
        saveBtn.setOnAction(e -> dialog.close());
        vbox.getChildren().add(saveBtn);
        dialog.setScene(new Scene(vbox, 350, 200));
        dialog.showAndWait();
    }

    // Popup sửa báo cáo doanh thu
    private void showEditSalesReportPopup(Object report) {
        Stage dialog = new Stage();
        dialog.setTitle("Sửa báo cáo doanh thu");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().add(new Label("Sửa thông tin báo cáo doanh thu"));
        // TODO: Hiển thị dữ liệu cũ, cho phép sửa
        Button saveBtn = new Button("Lưu");
        saveBtn.setOnAction(e -> dialog.close());
        vbox.getChildren().add(saveBtn);
        dialog.setScene(new Scene(vbox, 350, 200));
        dialog.showAndWait();
    }

    // Xác nhận xóa báo cáo doanh thu
    private void showDeleteSalesReportConfirm(Object report) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn xóa báo cáo này?");
        alert.showAndWait();
    }

    // Popup thêm báo cáo kho
    private void showAddInventoryReportPopup() {
        Stage dialog = new Stage();
        dialog.setTitle("Thêm báo cáo kho mới");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().add(new Label("Nhập thông tin báo cáo kho mới"));
        // TODO: Thêm các TextField nhập liệu
        Button saveBtn = new Button("Lưu");
        saveBtn.setOnAction(e -> dialog.close());
        vbox.getChildren().add(saveBtn);
        dialog.setScene(new Scene(vbox, 350, 200));
        dialog.showAndWait();
    }

    // Popup sửa báo cáo kho
    private void showEditInventoryReportPopup(Object report) {
        Stage dialog = new Stage();
        dialog.setTitle("Sửa báo cáo kho");
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().add(new Label("Sửa thông tin báo cáo kho"));
        // TODO: Hiển thị dữ liệu cũ, cho phép sửa
        Button saveBtn = new Button("Lưu");
        saveBtn.setOnAction(e -> dialog.close());
        vbox.getChildren().add(saveBtn);
        dialog.setScene(new Scene(vbox, 350, 200));
        dialog.showAndWait();
    }

    // Xác nhận xóa báo cáo kho
    private void showDeleteInventoryReportConfirm(Object report) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc muốn xóa báo cáo này?");
        alert.showAndWait();
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
