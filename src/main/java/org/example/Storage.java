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
        Label titleLabel = new Label("Storage Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button createNewButton = new Button("Create New Storage Item");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

        topContent.getChildren().addAll(titleLabel, createNewButton);
        topContent.setSpacing(10);
        topContent.setPadding(new Insets(10));

        // Thay thế TableView Storage bằng Inventory tổng hợp
        TableView<InventoryRow> inventoryTable = new TableView<>();
        inventoryTable.setPrefHeight(300);
        TableColumn<InventoryRow, String> bookIdCol = new TableColumn<>("Book ID");
        bookIdCol.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        TableColumn<InventoryRow, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<InventoryRow, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        TableColumn<InventoryRow, String> soldCol = new TableColumn<>("Sold Quantity");
        soldCol.setCellValueFactory(cellData -> cellData.getValue().soldQuantityProperty());
        TableColumn<InventoryRow, String> stockCol = new TableColumn<>("Stock Quantity");
        stockCol.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty());
        inventoryTable.getColumns().setAll(bookIdCol, titleCol, priceCol, soldCol, stockCol);
        inventoryTable.setItems(loadInventoryFromMongo());

        // CRUD buttons for Inventory Table
        Button addInventoryBtn = new Button("Thêm Inventory");
        Button editInventoryBtn = new Button("Sửa Inventory");
        Button deleteInventoryBtn = new Button("Xóa Inventory");
        editInventoryBtn.setDisable(true);
        deleteInventoryBtn.setDisable(true);
        HBox inventoryBtnBox = new HBox(10, addInventoryBtn, editInventoryBtn, deleteInventoryBtn);
        inventoryBtnBox.setPadding(new Insets(0, 0, 10, 0));
        // Enable/disable edit/delete buttons based on selection
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editInventoryBtn.setDisable(!selected);
            deleteInventoryBtn.setDisable(!selected);
        });

        // Thêm chức năng Thêm mới cho Inventory (Book)
        addInventoryBtn.setOnAction(e -> {
            // Dialog nhập liệu
            javafx.scene.control.Dialog<InventoryRow> dialog = new javafx.scene.control.Dialog<>();
            dialog.setTitle("Thêm Inventory");
            dialog.setHeaderText("Nhập thông tin Inventory");
            javafx.scene.control.ButtonType addBtnType = new javafx.scene.control.ButtonType("Thêm", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addBtnType, javafx.scene.control.ButtonType.CANCEL);
            VBox vbox = new VBox(10);
            vbox.setPadding(new Insets(10));
            javafx.scene.control.TextField bookIdField = new javafx.scene.control.TextField();
            bookIdField.setPromptText("Book ID");
            javafx.scene.control.TextField titleField = new javafx.scene.control.TextField();
            titleField.setPromptText("Title");
            javafx.scene.control.TextField priceField = new javafx.scene.control.TextField();
            priceField.setPromptText("Price");
            javafx.scene.control.TextField quantityField = new javafx.scene.control.TextField();
            quantityField.setPromptText("Quantity");
            vbox.getChildren().addAll(
                new Label("Book ID:"), bookIdField,
                new Label("Title:"), titleField,
                new Label("Price:"), priceField,
                new Label("Quantity:"), quantityField
            );
            dialog.getDialogPane().setContent(vbox);
            dialog.setResultConverter(dialogBtn -> {
                if (dialogBtn == addBtnType) {
                    return new InventoryRow(
                        bookIdField.getText().trim(),
                        titleField.getText().trim(),
                        priceField.getText().trim(),
                        "0", // Default sold quantity
                        quantityField.getText().trim()
                    );
                }
                return null;
            });
            dialog.showAndWait().ifPresent(inventoryRow -> {
                // Kiểm tra ràng buộc khóa ngoại
                String bookId = inventoryRow.bookIdProperty().get();
                try {
                    MongoDatabase db = MongoDBConnection.getDatabase();
                    MongoCollection<Document> booksCol = db.getCollection("Books");
                    Document book = booksCol.find(new Document("bookId", bookId)).first();
                    if (book == null) {
                        showError("Book ID không tồn tại!");
                        return;
                    }
                    // Insert vào Books
                    Document newDoc = new Document("bookId", bookId)
                        .append("title", inventoryRow.titleProperty().get())
                        .append("price", Double.parseDouble(inventoryRow.priceProperty().get()))
                        .append("quantity", Integer.parseInt(inventoryRow.stockQuantityProperty().get()));
                    booksCol.insertOne(newDoc);
                    // Reload bảng
                    inventoryTable.setItems(loadInventoryFromMongo());
                } catch (Exception ex) {
                    showError("Lỗi khi thêm Inventory: " + ex.getMessage());
                }
            });
        });

        // Xóa toàn bộ phần Order Details (label, bảng, nút CRUD) khỏi layout
        // Chỉ giữ lại Inventory và các thành phần liên quan
        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));

        Label inventoryLabel = new Label("Inventory:");
        inventoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        

        tablesSection.getChildren().clear();
        tablesSection.getChildren().addAll(inventoryLabel, inventoryBtnBox, inventoryTable);
     

        storageContent.setTop(topContent);
        storageContent.setCenter(tablesSection);
        return storageContent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Model for Inventory Table (Book)
    public static class InventoryRow {
        private SimpleStringProperty bookId, title, price, soldQuantity, stockQuantity;
        public InventoryRow(String bookId, String title, String price, String soldQuantity, String stockQuantity) {
            this.bookId = new SimpleStringProperty(bookId);
            this.title = new SimpleStringProperty(title);
            this.price = new SimpleStringProperty(price);
            this.soldQuantity = new SimpleStringProperty(soldQuantity);
            this.stockQuantity = new SimpleStringProperty(stockQuantity);
        }
        public SimpleStringProperty bookIdProperty() { return bookId; }
        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty priceProperty() { return price; }
        public SimpleStringProperty soldQuantityProperty() { return soldQuantity; }
        public SimpleStringProperty stockQuantityProperty() { return stockQuantity; }
    }
    // Model for Orders Table
    public static class OrderRow {
        private SimpleStringProperty orderId, orderDate, totalAmount, status, customerId;
        public OrderRow(String orderId, String orderDate, String totalAmount, String status, String customerId) {
            this.orderId = new SimpleStringProperty(orderId);
            this.orderDate = new SimpleStringProperty(orderDate);
            this.totalAmount = new SimpleStringProperty(totalAmount);
            this.status = new SimpleStringProperty(status);
            this.customerId = new SimpleStringProperty(customerId);
        }
        public SimpleStringProperty orderIdProperty() { return orderId; }
        public SimpleStringProperty orderDateProperty() { return orderDate; }
        public SimpleStringProperty totalAmountProperty() { return totalAmount; }
        public SimpleStringProperty statusProperty() { return status; }
        public SimpleStringProperty customerIdProperty() { return customerId; }
    }
    // Load Inventory (Book) from MongoDB
    private ObservableList<InventoryRow> loadInventoryFromMongo() {
        ObservableList<InventoryRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> booksCol = db.getCollection("Books");
            try (MongoCursor<Document> cursor = booksCol.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String bookId = doc.getString("bookId");
                    String title = doc.containsKey("title") ? doc.getString("title") : "";
                    String price = doc.containsKey("price") ? doc.get("price").toString() : "";
                    String quantity = doc.containsKey("quantity") ? doc.get("quantity").toString() : "";
                    list.add(new InventoryRow(bookId, title, price, "0", quantity)); // Default sold quantity to 0
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading inventory from MongoDB: " + e.getMessage());
        }
        return list;
    }
    // Load Orders from MongoDB
    private ObservableList<OrderRow> loadOrdersFromMongo() {
        ObservableList<OrderRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> col = db.getCollection("Orders");
            try (MongoCursor<Document> cursor = col.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String orderId = doc.getString("orderId");
                    String orderDate = doc.containsKey("orderDate") ? doc.getString("orderDate") : "";
                    String totalAmount = doc.containsKey("totalAmount") ? doc.get("totalAmount").toString() : "";
                    String status = doc.containsKey("status") ? doc.getString("status") : "";
                    String customerId = doc.containsKey("customerId") ? doc.getString("customerId") : "";
                    list.add(new OrderRow(orderId, orderDate, totalAmount, status, customerId));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading orders from MongoDB: " + e.getMessage());
        }
        return list;
    }

    private void showError(String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}