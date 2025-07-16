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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableCell;

public class Storage extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane storageContent = createStorageContent();
        Scene scene = new Scene(storageContent, 800, 600);
        primaryStage.setTitle("Storage Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public BorderPane createStorageContent() {
        BorderPane storageContent = new BorderPane();

        VBox topContent = new VBox();
        Label titleLabel = new Label("Quản lý kho");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        topContent.getChildren().addAll(titleLabel);
        topContent.setSpacing(10);
        topContent.setPadding(new Insets(10));

        // Thay thế TableView Storage bằng Inventory tổng hợp
        TableView<InventoryRow> inventoryTable = new TableView<>();
        inventoryTable.setPrefHeight(300);
        TableColumn<InventoryRow, String> bookIdCol = new TableColumn<>("Mã sách");
        bookIdCol.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        TableColumn<InventoryRow, String> titleCol = new TableColumn<>("Tiêu đề");
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<InventoryRow, String> priceCol = new TableColumn<>("Giá bán");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        TableColumn<InventoryRow, String> soldCol = new TableColumn<>("Đã bán");
        soldCol.setCellValueFactory(cellData -> cellData.getValue().soldQuantityProperty());
        TableColumn<InventoryRow, String> stockCol = new TableColumn<>("Tồn kho");
        stockCol.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty());
        inventoryTable.getColumns().setAll(bookIdCol, titleCol, priceCol, soldCol, stockCol);
        inventoryTable.setItems(loadInventoryFromMongo());

        // Thêm nút Thêm cho bảng kho sách
        Button addInventoryBtn = new Button("Thêm sách vào kho");
        addInventoryBtn.setOnAction(e -> showAddInventoryPopup());
        // Thêm icon sửa/xóa vào từng dòng bảng kho sách
        TableColumn<InventoryRow, Void> inventoryActionCol = new TableColumn<>("Thao tác");
        inventoryActionCol.setCellFactory(col -> new TableCell<InventoryRow, Void>() {
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();
            {
                editBtn.setGraphic(new Label("✏️"));
                editBtn.setStyle("-fx-background-color: transparent;");
                editBtn.setOnAction(ev -> showEditInventoryPopup(getTableView().getItems().get(getIndex())));
                deleteBtn.setGraphic(new Label("🗑️"));
                deleteBtn.setStyle("-fx-background-color: transparent;");
                deleteBtn.setOnAction(ev -> showDeleteInventoryConfirm(getTableView().getItems().get(getIndex())));
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
        inventoryTable.getColumns().add(inventoryActionCol);

        // Thêm bottom bar hiển thị tổng số sách
        Label lblTotal = new Label("Tổng số sách trong kho: " + inventoryTable.getItems().size());
        lblTotal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        VBox bottomBar = new VBox(lblTotal);
        bottomBar.setPadding(new Insets(10));
        storageContent.setBottom(bottomBar);

        storageContent.setTop(topContent);
        storageContent.setCenter(inventoryTable);
        return storageContent;
    }

    // Dummy methods for popup actions (bạn nên tự triển khai hoặc kết nối với UI thực tế)
    private void showAddInventoryPopup() {}
    private void showEditInventoryPopup(InventoryRow row) {}
    private void showDeleteInventoryConfirm(InventoryRow row) {}

    // Dummy method for loading data (bạn nên thay bằng truy vấn thực tế)
    private ObservableList<InventoryRow> loadInventoryFromMongo() {
        ObservableList<InventoryRow> list = FXCollections.observableArrayList();
        for (int i = 1; i <= 10; i++) {
            list.add(new InventoryRow("BID"+i, "Sách số "+i, "10000", String.valueOf(i*2), String.valueOf(20-i)));
        }
        return list;
    }

    // InventoryRow class (bạn nên tách ra file riêng nếu dùng nhiều)
    public static class InventoryRow {
        private final SimpleStringProperty bookId, title, price, soldQuantity, stockQuantity;
        public InventoryRow(String bookId, String title, String price, String sold, String stock) {
            this.bookId = new SimpleStringProperty(bookId);
            this.title = new SimpleStringProperty(title);
            this.price = new SimpleStringProperty(price);
            this.soldQuantity = new SimpleStringProperty(sold);
            this.stockQuantity = new SimpleStringProperty(stock);
        }
        public SimpleStringProperty bookIdProperty() { return bookId; }
        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty priceProperty() { return price; }
        public SimpleStringProperty soldQuantityProperty() { return soldQuantity; }
        public SimpleStringProperty stockQuantityProperty() { return stockQuantity; }
    }
}