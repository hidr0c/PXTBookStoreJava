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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Product extends Application {
    private static TableView<ProductRow> tvProduct;
    private static ObservableList<ProductRow> dataProduct = FXCollections.observableArrayList();
    private static TextField tfMaSP, tfTenSP, tfSoLuong, tfDonGia;
    private static Button btnAddProduct, btnEditProduct, btnDeleteProduct, btnClearProduct;

    @Override
    public void start(Stage primaryStage) {
        BorderPane productContent = createProductContent();
        Scene scene = new Scene(productContent, 800, 600);
        primaryStage.setTitle("Quản lý sản phẩm");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static BorderPane createProductContent() {
        BorderPane borderpane = new BorderPane();
        borderpane.setPadding(new Insets(30));
        borderpane.setTop(createProductForm());
        borderpane.setCenter(createProductTable());
        loadProduct();
        return borderpane;
    }

    private static VBox createProductForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        Label title = new Label("Quản lý sản phẩm");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox row1 = new HBox(10);
        tfMaSP = new TextField(); tfMaSP.setPromptText("Mã sản phẩm");
        tfTenSP = new TextField(); tfTenSP.setPromptText("Tên sản phẩm");
        row1.getChildren().addAll(new Label("Mã SP:"), tfMaSP, new Label("Tên SP:"), tfTenSP);
        HBox row2 = new HBox(10);
        tfSoLuong = new TextField(); tfSoLuong.setPromptText("Số lượng");
        tfDonGia = new TextField(); tfDonGia.setPromptText("Đơn giá");
        row2.getChildren().addAll(new Label("Số lượng:"), tfSoLuong, new Label("Đơn giá:"), tfDonGia);
        HBox row3 = new HBox(10);
        btnAddProduct = new Button("Thêm");
        btnEditProduct = new Button("Sửa");
        btnDeleteProduct = new Button("Xóa");
        btnClearProduct = new Button("Clear");
        btnEditProduct.setDisable(true);
        btnDeleteProduct.setDisable(true);
        row3.getChildren().addAll(btnAddProduct, btnEditProduct, btnDeleteProduct, btnClearProduct);
        form.getChildren().addAll(title, row1, row2, row3);

        btnAddProduct.setOnAction(e -> saveProduct());
        btnEditProduct.setOnAction(e -> editProduct());
        btnDeleteProduct.setOnAction(e -> deleteProduct());
        btnClearProduct.setOnAction(e -> clearProductForm());
        return form;
    }

    private static TableView<ProductRow> createProductTable() {
        tvProduct = new TableView<>();
        tvProduct.setPrefHeight(350);

        TableColumn<ProductRow, String> colMaSP = new TableColumn<>("Mã sản phẩm");
        colMaSP.setCellValueFactory((p) -> {
            ProductRow sp = p.getValue();
            String ma = sp.getMaSP();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(ma);
        });

        TableColumn<ProductRow, String> colTenSP = new TableColumn<>("Tên sản phẩm");
        colTenSP.setCellValueFactory((p) -> {
            ProductRow sp = p.getValue();
            String ten = sp.getTenSP();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(ten);
        });

        TableColumn<ProductRow, Integer> colSoLuong = new TableColumn<>("Số lượng");
        colSoLuong.setCellValueFactory((p) -> {
            ProductRow sp = p.getValue();
            int sl = sp.getSoLuong();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(sl);
        });

        TableColumn<ProductRow, Float> colDonGia = new TableColumn<>("Đơn giá");
        colDonGia.setCellValueFactory((p) -> {
            ProductRow sp = p.getValue();
            float dg = sp.getDonGia();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(dg);
        });

        TableColumn<ProductRow, Float> colTienGiam = new TableColumn<>("Tiền giảm");
        colTienGiam.setCellValueFactory((p) -> {
            ProductRow sp = p.getValue();
            float gg = sp.getTienGiam();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(gg);
        });

        TableColumn<ProductRow, Float> colThanhTien = new TableColumn<>("Tiền phải trả");
        colThanhTien.setCellValueFactory((p) -> {
            ProductRow sp = p.getValue();
            float tt = sp.getThanhTien();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(tt);
        });
        tvProduct.getColumns().addAll(colMaSP, colTenSP, colSoLuong, colDonGia, colTienGiam, colThanhTien);
        tvProduct.setItems(dataProduct);
        tvProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            btnEditProduct.setDisable(!selected);
            btnDeleteProduct.setDisable(!selected);
            if (selected) showProductItem(newSel);
        });
        return tvProduct;
    }

    private static void loadProduct() {
        dataProduct.clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT bookID, title, price, quantity FROM Books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String ma = rs.getString("bookID");
                String ten = rs.getString("title");
                int sl = rs.getInt("quantity");
                float dg = rs.getFloat("price");
                float tt = dg * sl; // Tổng tiền = đơn giá * số lượng
                dataProduct.add(new ProductRow(ma, ten, sl, dg, 0, tt));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveProduct() {
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Lưu sản phẩm!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (tfMaSP.getText().isEmpty() || tfTenSP.getText().isEmpty() ||
                tfSoLuong.getText().isEmpty() || tfDonGia.getText().isEmpty()) {
                thongbao.setContentText("Vui lòng nhập đầy đủ thông tin!");
                thongbao.show();
                return;
            }
            String sql = "INSERT INTO Books(bookID, title, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfMaSP.getText());
            ps.setString(2, tfTenSP.getText());
            ps.setFloat(3, Float.parseFloat(tfDonGia.getText()));
            ps.setInt(4, Integer.parseInt(tfSoLuong.getText()));
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Lưu sản phẩm thành công!");
                thongbao.show();
                loadProduct();
                clearProductForm();
            } else {
                thongbao.setContentText("Lưu sản phẩm thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }

    private static void editProduct() {
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Sửa sản phẩm!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "UPDATE Books SET title=?, price=?, quantity=? WHERE bookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfTenSP.getText());
            ps.setFloat(2, Float.parseFloat(tfDonGia.getText()));
            ps.setInt(3, Integer.parseInt(tfSoLuong.getText()));
            ps.setString(4, tfMaSP.getText()); // Lấy mã sản phẩm từ TextField
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Sửa sản phẩm thành công!");
                thongbao.show();
                loadProduct();
                clearProductForm();
            } else {
                thongbao.setContentText("Sửa sản phẩm thất bại!");
                thongbao.show();
            }
            ps.close();
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }

    private static void deleteProduct() {
        ProductRow selected = tvProduct.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Xóa sản phẩm!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "DELETE FROM Books WHERE bookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, selected.getMaSP());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Xóa sản phẩm thành công!");
                thongbao.show();
                loadProduct();
                clearProductForm();
            } else {
                thongbao.setContentText("Xóa sản phẩm thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }

    private static void clearProductForm() {
        tfMaSP.clear();
        tfTenSP.clear();
        tfSoLuong.clear();
        tfDonGia.clear();
        tvProduct.getSelectionModel().clearSelection();
        btnEditProduct.setDisable(true);
        btnDeleteProduct.setDisable(true);
    }

    private static void showProductItem(ProductRow row) {
        if (row == null) return;
        tfMaSP.setText(row.getMaSP());
        tfTenSP.setText(row.getTenSP());
        tfSoLuong.setText(String.valueOf(row.getSoLuong()));
        tfDonGia.setText(String.valueOf(row.getDonGia()));
    }

    public static class ProductRow {
        private final SimpleStringProperty maSP;
        private final SimpleStringProperty tenSP;
        private final javafx.beans.property.SimpleIntegerProperty soLuong;
        private final javafx.beans.property.SimpleFloatProperty donGia;
        private final javafx.beans.property.SimpleFloatProperty tienGiam;
        private final javafx.beans.property.SimpleFloatProperty thanhTien;
        public ProductRow(String maSP, String tenSP, int soLuong, float donGia, float tienGiam, float thanhTien) {
            this.maSP = new SimpleStringProperty(maSP);
            this.tenSP = new SimpleStringProperty(tenSP);
            this.soLuong = new javafx.beans.property.SimpleIntegerProperty(soLuong);
            this.donGia = new javafx.beans.property.SimpleFloatProperty(donGia);
            this.tienGiam = new javafx.beans.property.SimpleFloatProperty(tienGiam);
            this.thanhTien = new javafx.beans.property.SimpleFloatProperty(thanhTien);
        }
        public String getMaSP() { return maSP.get(); }
        public String getTenSP() { return tenSP.get(); }
        public int getSoLuong() { return soLuong.get(); }
        public float getDonGia() { return donGia.get(); }
        public float getTienGiam() { return tienGiam.get(); }
        public float getThanhTien() { return thanhTien.get(); }
        public SimpleStringProperty maSPProperty() { return maSP; }
        public SimpleStringProperty tenSPProperty() { return tenSP; }
        public javafx.beans.property.SimpleIntegerProperty soLuongProperty() { return soLuong; }
        public javafx.beans.property.SimpleFloatProperty donGiaProperty() { return donGia; }
        public javafx.beans.property.SimpleFloatProperty tienGiamProperty() { return tienGiam; }
        public javafx.beans.property.SimpleFloatProperty thanhTienProperty() { return thanhTien; }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 