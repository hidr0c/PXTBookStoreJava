package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.sql.*;

public class Storage extends Application {
    private static TableView<StorageRow> tvStorage;
    private static ObservableList<StorageRow> dataStorage = FXCollections.observableArrayList();
    private static ComboBox<String> cbBookID;
    private static TextField tfQuantity;
    private static Button btnAddStorage, btnEditStorage, btnDeleteStorage, btnClearStorage;

    @Override
    public void start(Stage primaryStage) {
        BorderPane storageContent = createStorageContent();
        Scene scene = new Scene(storageContent, 800, 600);
        primaryStage.setTitle("Quản lý kho");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static BorderPane createStorageContent() {
        BorderPane borderpane = new BorderPane();
        borderpane.setPadding(new Insets(30));
        borderpane.setTop(createStorageForm());
        borderpane.setCenter(createStorageTable());
        loadStorage();
        return borderpane;
    }

    private static VBox createStorageForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        Label title = new Label("Quản lý kho");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox row1 = new HBox(10);
        cbBookID = new ComboBox<>(); cbBookID.setPromptText("Mã sách");
        loadBookIDs();
        row1.getChildren().addAll(new Label("Mã sách:"), cbBookID);
        HBox row2 = new HBox(10);
        tfQuantity = new TextField(); tfQuantity.setPromptText("Số lượng nhập");
        row2.getChildren().addAll(new Label("Số lượng nhập:"), tfQuantity);
        HBox row3 = new HBox(10);
        btnAddStorage = new Button("Thêm");
        btnEditStorage = new Button("Sửa");
        btnDeleteStorage = new Button("Xóa");
        btnClearStorage = new Button("Clear");
        btnEditStorage.setDisable(true);
        btnDeleteStorage.setDisable(true);
        row3.getChildren().addAll(btnAddStorage, btnEditStorage, btnDeleteStorage, btnClearStorage);
        form.getChildren().addAll(title, row1, row2, row3);
        btnAddStorage.setOnAction(e -> saveStorage());
        btnEditStorage.setOnAction(e -> editStorage());
        btnDeleteStorage.setOnAction(e -> deleteStorage());
        btnClearStorage.setOnAction(e -> clearStorageForm());
        return form;
    }

    private static TableView<StorageRow> createStorageTable() {
        tvStorage = new TableView<>();
        tvStorage.setPrefHeight(350);
        TableColumn<StorageRow, String> colBookID = new TableColumn<>("Mã sách");
        colBookID.setCellValueFactory((p) -> {
            StorageRow s = p.getValue();
            String id = s.getBookID();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(id);
        });
        TableColumn<StorageRow, String> colBookName = new TableColumn<>("Tên sách");
        colBookName.setCellValueFactory((p) -> {
            StorageRow s = p.getValue();
            String name = s.getBookName();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(name);
        });
        TableColumn<StorageRow, Integer> colQuantity = new TableColumn<>("Số lượng nhập");
        colQuantity.setCellValueFactory((p) -> {
            StorageRow s = p.getValue();
            int quantity = s.getQuantity();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(quantity);
        });
        TableColumn<StorageRow, Integer> colSold = new TableColumn<>("Đã bán");
        colSold.setCellValueFactory((p) -> {
            StorageRow s = p.getValue();
            int sold = s.getSoldQuantity();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(sold);
        });
        TableColumn<StorageRow, Integer> colStock = new TableColumn<>("Tồn kho");
        colStock.setCellValueFactory((p) -> {
            StorageRow s = p.getValue();
            int stock = s.getStockQuantity();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(stock);
        });
        tvStorage.getColumns().addAll(colBookID, colBookName, colQuantity, colSold, colStock);
        tvStorage.setItems(dataStorage);
        tvStorage.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            btnEditStorage.setDisable(!selected);
            btnDeleteStorage.setDisable(!selected);
            if (selected) showStorageItem(newSel);
        });
        return tvStorage;
    }

    private static void loadStorage() {
        dataStorage.clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT bookID, bookName, quantity, soldQuantity, stockQuantity FROM Storages";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("bookID");
                String name = rs.getString("bookName");
                int quantity = rs.getInt("quantity");
                int sold = rs.getInt("soldQuantity");
                int stock = rs.getInt("stockQuantity");
                dataStorage.add(new StorageRow(id, name, quantity, sold, stock));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadBookIDs() {
        cbBookID.getItems().clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT bookID FROM Books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                cbBookID.getItems().add(rs.getString("bookID"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveStorage() {
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Lưu kho!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (cbBookID.getValue() == null || tfQuantity.getText().isEmpty()) {
                thongbao.setContentText("Vui lòng nhập đầy đủ thông tin!");
                thongbao.show();
                return;
            }
            // Lấy tên sách từ bảng Books
            String sqlBook = "SELECT title FROM Books WHERE bookID = ?";
            PreparedStatement psBook = conn.prepareStatement(sqlBook);
            psBook.setString(1, cbBookID.getValue());
            ResultSet rsBook = psBook.executeQuery();
            String bookName = null;
            if (rsBook.next()) {
                bookName = rsBook.getString("title");
            } else {
                thongbao.setContentText("Mã sách không tồn tại, vui lòng thêm ở mục Sản phẩm!");
                thongbao.show();
                rsBook.close();
                psBook.close();
                return;
            }
            rsBook.close();
            psBook.close();
            // Thêm vào Storages
            String sql = "INSERT INTO Storages(bookID, bookName, quantity, soldQuantity, stockQuantity) VALUES (?, ?, ?, 0, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cbBookID.getValue());
            ps.setString(2, bookName);
            int quantity = Integer.parseInt(tfQuantity.getText());
            ps.setInt(3, quantity);
            ps.setInt(4, quantity); // stockQuantity ban đầu = quantity
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Lưu kho thành công!");
                thongbao.show();
                loadStorage();
                clearStorageForm();
            } else {
                thongbao.setContentText("Lưu kho thất bại!");
                thongbao.show();
            }
            ps.close();
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }

    private static void editStorage() {
        StorageRow selected = tvStorage.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Sửa kho!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (tfQuantity.getText().isEmpty()) {
                thongbao.setContentText("Vui lòng nhập số lượng nhập!");
                thongbao.show();
                return;
            }
            // Không cập nhật tên sách, chỉ cập nhật quantity
            String sql = "UPDATE Storages SET quantity=? WHERE bookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(tfQuantity.getText()));
            ps.setString(2, cbBookID.getValue()); // Use cbBookID.getValue()
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Sửa kho thành công!");
                thongbao.show();
                loadStorage();
                clearStorageForm();
            } else {
                thongbao.setContentText("Sửa kho thất bại!");
                thongbao.show();
            }
            ps.close();
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }

    private static void deleteStorage() {
        StorageRow selected = tvStorage.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Xóa kho!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "DELETE FROM Storages WHERE bookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, selected.getBookID());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Xóa kho thành công!");
                thongbao.show();
                loadStorage();
                clearStorageForm();
            } else {
                thongbao.setContentText("Xóa kho thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }

    private static void clearStorageForm() {
        cbBookID.setValue(null);
        tfQuantity.clear();
        tvStorage.getSelectionModel().clearSelection();
        btnEditStorage.setDisable(true);
        btnDeleteStorage.setDisable(true);
    }

    private static void showStorageItem(StorageRow row) {
        if (row == null) return;
        cbBookID.setValue(row.getBookID());
        tfQuantity.setText(String.valueOf(row.getQuantity()));
    }

    public static class StorageRow {
        private final SimpleStringProperty bookID;
        private final SimpleStringProperty bookName;
        private final SimpleIntegerProperty quantity;
        private final SimpleIntegerProperty soldQuantity;
        private final SimpleIntegerProperty stockQuantity;
        public StorageRow(String bookID, String bookName, int quantity, int soldQuantity, int stockQuantity) {
            this.bookID = new SimpleStringProperty(bookID);
            this.bookName = new SimpleStringProperty(bookName);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.soldQuantity = new SimpleIntegerProperty(soldQuantity);
            this.stockQuantity = new SimpleIntegerProperty(stockQuantity);
        }
        public String getBookID() { return bookID.get(); }
        public String getBookName() { return bookName.get(); }
        public int getQuantity() { return quantity.get(); }
        public int getSoldQuantity() { return soldQuantity.get(); }
        public int getStockQuantity() { return stockQuantity.get(); }
        public SimpleStringProperty bookIDProperty() { return bookID; }
        public SimpleStringProperty bookNameProperty() { return bookName; }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        public SimpleIntegerProperty soldQuantityProperty() { return soldQuantity; }
        public SimpleIntegerProperty stockQuantityProperty() { return stockQuantity; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}




