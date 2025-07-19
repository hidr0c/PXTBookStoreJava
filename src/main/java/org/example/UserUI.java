package org.example;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleFloatProperty;
import java.sql.*;

public class UserUI {
    // Staff
    private static TableView<StaffRow> tvStaff;
    private static ObservableList<StaffRow> dataStaff = FXCollections.observableArrayList();
    private static TextField tfStaffID;
    private static ComboBox<String> cbPosition;
    private static Button btnAddStaff, btnEditStaff, btnDeleteStaff, btnClearStaff;
    // Customer
    private static TableView<CustomerRow> tvCustomer;
    private static ObservableList<CustomerRow> dataCustomer = FXCollections.observableArrayList();
    private static TextField tfCustomerID;
    private static ComboBox<String> cbRank;
    private static TextField tfSpending;
    private static Button btnAddCustomer, btnEditCustomer, btnDeleteCustomer, btnClearCustomer;

    public static BorderPane createUserContent() {
        BorderPane borderpane = new BorderPane();
        borderpane.setPadding(new Insets(30));
        VBox vbox = new VBox(30);
        vbox.getChildren().addAll(createStaffSection(), createCustomerSection());
        borderpane.setCenter(vbox);
        loadStaff();
        loadCustomer();
        return borderpane;
    }

    // Staff Section
    private static VBox createStaffSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        Label title = new Label("Quản lý nhân viên (Staff)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox row1 = new HBox(10);
        tfStaffID = new TextField(); tfStaffID.setPromptText("Mã nhân viên");
        cbPosition = new ComboBox<>(); cbPosition.setPromptText("Chức vụ");
        cbPosition.getItems().addAll("Manager", "Salesperson", "Cashier", "Support", "Inventory Manager", "Marketing");
        row1.getChildren().addAll(new Label("Mã NV:"), tfStaffID, new Label("Chức vụ:"), cbPosition);
        HBox row2 = new HBox(10);
        btnAddStaff = new Button("Thêm");
        btnEditStaff = new Button("Sửa");
        btnDeleteStaff = new Button("Xóa");
        btnClearStaff = new Button("Clear");
        btnEditStaff.setDisable(true);
        btnDeleteStaff.setDisable(true);
        row2.getChildren().addAll(btnAddStaff, btnEditStaff, btnDeleteStaff, btnClearStaff);
        section.getChildren().addAll(title, row1, row2, createStaffTable());
        btnAddStaff.setOnAction(e -> saveStaff());
        btnEditStaff.setOnAction(e -> editStaff());
        btnDeleteStaff.setOnAction(e -> deleteStaff());
        btnClearStaff.setOnAction(e -> clearStaffForm());
        return section;
    }
    private static TableView<StaffRow> createStaffTable() {
        tvStaff = new TableView<>();
        tvStaff.setPrefHeight(200);
        TableColumn<StaffRow, String> colStaffID = new TableColumn<>("Mã nhân viên");
        colStaffID.setCellValueFactory((p) -> {
            StaffRow s = p.getValue();
            String id = s.getStaffID();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(id);
        });
        TableColumn<StaffRow, String> colPosition = new TableColumn<>("Chức vụ");
        colPosition.setCellValueFactory((p) -> {
            StaffRow s = p.getValue();
            String pos = s.getPosition();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(pos);
        });
        tvStaff.getColumns().addAll(colStaffID, colPosition);
        tvStaff.setItems(dataStaff);
        tvStaff.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            btnEditStaff.setDisable(!selected);
            btnDeleteStaff.setDisable(!selected);
            if (selected) showStaffItem(newSel);
        });
        return tvStaff;
    }
    private static void loadStaff() {
        dataStaff.clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT staffID, position FROM Staffs";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            int count = 0;
            while (rs.next()) {
                String id = rs.getString("staffID");
                String pos = rs.getString("position");
                dataStaff.add(new StaffRow(id, pos));
                count++;
            }
            System.out.println("Loaded " + count + " staff records from database");
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void saveStaff() {
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Lưu nhân viên!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (tfStaffID.getText().isEmpty() || cbPosition.getValue() == null) {
                thongbao.setContentText("Vui lòng nhập đầy đủ thông tin!");
                thongbao.show();
                return;
            }
            
            // Kiểm tra xem user đã tồn tại trong Users chưa
            String checkSql = "SELECT COUNT(*) FROM Users WHERE userID = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, tfStaffID.getText());
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Nếu user chưa có trong Users, thêm vào trước
                String insertUserSql = "INSERT INTO Users(userID, fullName, address, phoneNumber) VALUES (?, ?, '', '')";
                PreparedStatement userPs = conn.prepareStatement(insertUserSql);
                userPs.setString(1, tfStaffID.getText());
                userPs.setString(2, "Staff " + tfStaffID.getText()); // Tên mặc định
                userPs.executeUpdate();
                userPs.close();
            }
            
            // Sau đó thêm vào Staffs
            String sql = "INSERT INTO Staffs(staffID, position) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfStaffID.getText());
            ps.setString(2, cbPosition.getValue());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Lưu nhân viên thành công!");
                thongbao.show();
                loadStaff();
                clearStaffForm();
            } else {
                thongbao.setContentText("Lưu nhân viên thất bại!");
                thongbao.show();
            }
            
            checkPs.close();
            rs.close();
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }
    private static void editStaff() {
        StaffRow selected = tvStaff.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Sửa nhân viên!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (cbPosition.getValue() == null) {
                thongbao.setContentText("Vui lòng chọn chức vụ!");
                thongbao.show();
                return;
            }
            String sql = "UPDATE Staffs SET position=? WHERE staffID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cbPosition.getValue());
            ps.setString(2, selected.getStaffID()); // Sử dụng staffID từ row được chọn
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Sửa nhân viên thành công! StaffID: " + selected.getStaffID() + ", Position: " + cbPosition.getValue());
                thongbao.show();
                // Force refresh TableView - sử dụng Platform.runLater để đảm bảo chạy trên JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    dataStaff.clear();
                    loadStaff();
                    tvStaff.refresh();
                });
                clearStaffForm();
            } else {
                thongbao.setContentText("Sửa nhân viên thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }
    private static void deleteStaff() {
        StaffRow selected = tvStaff.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Xóa nhân viên!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "DELETE FROM Staffs WHERE staffID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, selected.getStaffID());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Xóa nhân viên thành công!");
                thongbao.show();
                loadStaff();
                clearStaffForm();
            } else {
                thongbao.setContentText("Xóa nhân viên thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }
    private static void clearStaffForm() {
        tfStaffID.clear();
        cbPosition.setValue(null);
        tvStaff.getSelectionModel().clearSelection();
        btnEditStaff.setDisable(true);
        btnDeleteStaff.setDisable(true);
    }
    private static void showStaffItem(StaffRow row) {
        if (row == null) return;
        tfStaffID.setText(row.getStaffID());
        cbPosition.setValue(row.getPosition());
    }
    public static class StaffRow {
        private final SimpleStringProperty staffID;
        private final SimpleStringProperty position;
        public StaffRow(String staffID, String position) {
            this.staffID = new SimpleStringProperty(staffID);
            this.position = new SimpleStringProperty(position);
        }
        public String getStaffID() { return staffID.get(); }
        public String getPosition() { return position.get(); }
        public SimpleStringProperty staffIDProperty() { return staffID; }
        public SimpleStringProperty positionProperty() { return position; }
    }

    // Customer Section
    private static VBox createCustomerSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        Label title = new Label("Quản lý khách hàng (Customer)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        HBox row1 = new HBox(10);
        tfCustomerID = new TextField(); tfCustomerID.setPromptText("Mã khách hàng");
        cbRank = new ComboBox<>(); cbRank.setPromptText("Hạng");
        cbRank.getItems().addAll("Bronze", "Silver", "Gold", "Platinum");
        tfSpending = new TextField(); tfSpending.setPromptText("Chi tiêu");
        row1.getChildren().addAll(new Label("Mã KH:"), tfCustomerID, new Label("Hạng:"), cbRank, new Label("Chi tiêu:"), tfSpending);
        HBox row2 = new HBox(10);
        btnAddCustomer = new Button("Thêm");
        btnEditCustomer = new Button("Sửa");
        btnDeleteCustomer = new Button("Xóa");
        btnClearCustomer = new Button("Clear");
        btnEditCustomer.setDisable(true);
        btnDeleteCustomer.setDisable(true);
        row2.getChildren().addAll(btnAddCustomer, btnEditCustomer, btnDeleteCustomer, btnClearCustomer);
        section.getChildren().addAll(title, row1, row2, createCustomerTable());
        btnAddCustomer.setOnAction(e -> saveCustomer());
        btnEditCustomer.setOnAction(e -> editCustomer());
        btnDeleteCustomer.setOnAction(e -> deleteCustomer());
        btnClearCustomer.setOnAction(e -> clearCustomerForm());
        return section;
    }
    private static TableView<CustomerRow> createCustomerTable() {
        tvCustomer = new TableView<>();
        tvCustomer.setPrefHeight(200);
        TableColumn<CustomerRow, String> colCustomerID = new TableColumn<>("Mã khách hàng");
        colCustomerID.setCellValueFactory((p) -> {
            CustomerRow c = p.getValue();
            String id = c.getCustomerID();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(id);
        });
        TableColumn<CustomerRow, String> colRank = new TableColumn<>("Hạng");
        colRank.setCellValueFactory((p) -> {
            CustomerRow c = p.getValue();
            String rank = c.getRank();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(rank);
        });
        TableColumn<CustomerRow, Float> colSpending = new TableColumn<>("Chi tiêu");
        colSpending.setCellValueFactory((p) -> {
            CustomerRow c = p.getValue();
            float spending = c.getSpending();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(spending);
        });
        tvCustomer.getColumns().addAll(colCustomerID, colRank, colSpending);
        tvCustomer.setItems(dataCustomer);
        tvCustomer.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            btnEditCustomer.setDisable(!selected);
            btnDeleteCustomer.setDisable(!selected);
            if (selected) showCustomerItem(newSel);
        });
        return tvCustomer;
    }
    private static void loadCustomer() {
        dataCustomer.clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT customerID, rankC, spending FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String id = rs.getString("customerID");
                String rank = rs.getString("rankC");
                float spending = rs.getFloat("spending");
                dataCustomer.add(new CustomerRow(id, rank, spending));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void saveCustomer() {
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Lưu khách hàng!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (tfCustomerID.getText().isEmpty() || cbRank.getValue() == null || tfSpending.getText().isEmpty()) {
                thongbao.setContentText("Vui lòng nhập đầy đủ thông tin!");
                thongbao.show();
                return;
            }
            
            // Kiểm tra xem user đã tồn tại trong Users chưa
            String checkSql = "SELECT COUNT(*) FROM Users WHERE userID = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, tfCustomerID.getText());
            ResultSet rs = checkPs.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            if (count == 0) {
                // Nếu user chưa có trong Users, thêm vào trước
                String insertUserSql = "INSERT INTO Users(userID, fullName, address, phoneNumber) VALUES (?, ?, '', '')";
                PreparedStatement userPs = conn.prepareStatement(insertUserSql);
                userPs.setString(1, tfCustomerID.getText());
                userPs.setString(2, "Customer " + tfCustomerID.getText()); // Tên mặc định
                userPs.executeUpdate();
                userPs.close();
            }
            
            // Sau đó thêm vào Customers
            String sql = "INSERT INTO Customers(customerID, rankC, spending) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfCustomerID.getText());
            ps.setString(2, cbRank.getValue());
            ps.setFloat(3, Float.parseFloat(tfSpending.getText()));
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Lưu khách hàng thành công!");
                thongbao.show();
                loadCustomer();
                clearCustomerForm();
            } else {
                thongbao.setContentText("Lưu khách hàng thất bại!");
                thongbao.show();
            }
            
            checkPs.close();
            rs.close();
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }
    private static void editCustomer() {
        CustomerRow selected = tvCustomer.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Sửa khách hàng!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            if (cbRank.getValue() == null) {
                thongbao.setContentText("Vui lòng chọn hạng!");
                thongbao.show();
                return;
            }
            String sql = "UPDATE Customers SET rankC=?, spending=? WHERE customerID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, cbRank.getValue());
            ps.setFloat(2, Float.parseFloat(tfSpending.getText()));
            ps.setString(3, selected.getCustomerID()); // Sử dụng customerID từ row được chọn
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Sửa khách hàng thành công! CustomerID: " + selected.getCustomerID() + ", Rank: " + cbRank.getValue() + ", Spending: " + tfSpending.getText());
                thongbao.show();
                // Force refresh TableView - sử dụng Platform.runLater để đảm bảo chạy trên JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    dataCustomer.clear();
                    loadCustomer();
                    tvCustomer.refresh();
                });
                clearCustomerForm();
            } else {
                thongbao.setContentText("Sửa khách hàng thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }
    private static void deleteCustomer() {
        CustomerRow selected = tvCustomer.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Xóa khách hàng!!!");
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "DELETE FROM Customers WHERE customerID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, selected.getCustomerID());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                thongbao.setContentText("Xóa khách hàng thành công!");
                thongbao.show();
                loadCustomer();
                clearCustomerForm();
            } else {
                thongbao.setContentText("Xóa khách hàng thất bại!");
                thongbao.show();
            }
        } catch (Exception e) {
            thongbao.setContentText("Lỗi: " + e.getMessage());
            thongbao.show();
        }
    }
    private static void clearCustomerForm() {
        tfCustomerID.clear();
        cbRank.setValue(null);
        tfSpending.clear();
        tvCustomer.getSelectionModel().clearSelection();
        btnEditCustomer.setDisable(true);
        btnDeleteCustomer.setDisable(true);
    }
    private static void showCustomerItem(CustomerRow row) {
        if (row == null) return;
        tfCustomerID.setText(row.getCustomerID());
        cbRank.setValue(row.getRank());
        tfSpending.setText(String.valueOf(row.getSpending()));
    }
    public static class CustomerRow {
        private final SimpleStringProperty customerID;
        private final SimpleStringProperty rank;
        private final SimpleFloatProperty spending;
        public CustomerRow(String customerID, String rank, float spending) {
            this.customerID = new SimpleStringProperty(customerID);
            this.rank = new SimpleStringProperty(rank);
            this.spending = new SimpleFloatProperty(spending);
        }
        public String getCustomerID() { return customerID.get(); }
        public String getRank() { return rank.get(); }
        public float getSpending() { return spending.get(); }
        public SimpleStringProperty customerIDProperty() { return customerID; }
        public SimpleStringProperty rankProperty() { return rank; }
        public SimpleFloatProperty spendingProperty() { return spending; }
    }
} 