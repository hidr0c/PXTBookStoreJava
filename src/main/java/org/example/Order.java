package org.example;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleFloatProperty;

public class Order extends Application {
    private String orderID;
    private String orderDate;
    private float total;
    private String status;
    private String customerID;
    private String staffID;

    public Order(String orderID, String orderDate, float total, String status, String customerID, String staffID) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.total = total;
        this.status = status;
        this.customerID = customerID;
        this.staffID = staffID;
    }

    public Order() {
        // Constructor mặc định để dùng cho JavaFX Application
    }

    public String getOrderID() { return orderID; }
    public String getOrderDate() { return orderDate; }
    public float getTotal() { return total; }
    public String getStatus() { return status; }
    public String getCustomerID() { return customerID; }
    public String getStaffID() { return staffID; }

    // --- CRUD UI & Logic ---
    static TextField tfOrderID, tfOrderDate, tfTotal, tfStatus;
    static TableView<Order> tvOrder;
    static ObservableList<Order> dataOrder = FXCollections.observableArrayList();
    static Button btnAddOrder, btnEditOrder, btnDeleteOrder, btnClearOrder;

    private static TableView<OrderDetailRow> tvOrderDetail;
    private static ObservableList<OrderDetailRow> dataOrderDetail = FXCollections.observableArrayList();

    // Thay TextField bằng ComboBox cho CustomerID và StaffID
    static ComboBox<String> cbCustomerID, cbStaffID;
    // Thêm biến cho BookID và Quantity
    static ComboBox<String> cbBookID;
    static TextField tfQuantity;
    static ComboBox<String> cbStatus;

    @Override
    public void start(Stage stage) {
        BorderPane root = createOrderContent();
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Quản lý Đơn hàng");
        stage.show();
    }

    public static VBox createOrderForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        Label title = new Label("Quản lý hóa đơn");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox row1 = new HBox(10);
        tfOrderID = new TextField(); tfOrderID.setPromptText("Order ID");
        cbBookID = new ComboBox<>(); cbBookID.setPromptText("Book ID");
        row1.getChildren().addAll(new Label("Order ID:"), tfOrderID, new Label("Book ID:"), cbBookID);
        HBox row2 = new HBox(10);
        cbStatus = new ComboBox<>(); cbStatus.setPromptText("Status");
        cbStatus.getItems().addAll("Pending", "Processing", "Completed", "Cancelled");
        cbCustomerID = new ComboBox<>(); cbCustomerID.setPromptText("Customer ID");
        cbStaffID = new ComboBox<>(); cbStaffID.setPromptText("Staff ID");
        tfQuantity = new TextField(); tfQuantity.setPromptText("Quantity");
        row2.getChildren().addAll(new Label("Status:"), cbStatus, new Label("Customer ID:"), cbCustomerID, new Label("Staff ID:"), cbStaffID, new Label("Quantity:"), tfQuantity);
        HBox row3 = new HBox(10);
        btnAddOrder = new Button("Thêm");
        btnEditOrder = new Button("Sửa");
        btnDeleteOrder = new Button("Xóa");
        btnClearOrder = new Button("Xóa tất cả");
        row3.getChildren().addAll(btnAddOrder, btnEditOrder, btnDeleteOrder, btnClearOrder);
        form.getChildren().addAll(title, row1, row2, row3);
        // Load dữ liệu cho ComboBox
        loadCustomerIDs();
        loadStaffIDs();
        loadBookIDs();
        return form;
    }

    public static StackPane showTableOrder() {
        StackPane layout = new StackPane();
        tvOrder = new TableView<>();
        tvOrder.setPrefHeight(350);

        TableColumn<Order, String> colOrderID = new TableColumn<>("Mã đơn hàng");
        colOrderID.setCellValueFactory((p) -> {
            Order order = p.getValue();
            String id = order.getOrderID();
            return new ReadOnlyObjectWrapper<>(id);
        });

        TableColumn<Order, String> colOrderDate = new TableColumn<>("Ngày đặt");
        colOrderDate.setCellValueFactory((p) -> {
            Order order = p.getValue();
            String date = order.getOrderDate();
            return new ReadOnlyObjectWrapper<>(date);
        });

        TableColumn<Order, Float> colTotal = new TableColumn<>("Tổng tiền");
        colTotal.setCellValueFactory((p) -> {
            Order order = p.getValue();
            float total = order.getTotal();
            return new ReadOnlyObjectWrapper<>(total);
        });

        TableColumn<Order, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory((p) -> {
            Order order = p.getValue();
            String status = order.getStatus();
            return new ReadOnlyObjectWrapper<>(status);
        });

        TableColumn<Order, String> colCustomerID = new TableColumn<>("Mã khách hàng");
        colCustomerID.setCellValueFactory((p) -> {
            Order order = p.getValue();
            String customer = order.getCustomerID();
            return new ReadOnlyObjectWrapper<>(customer);
        });

        TableColumn<Order, String> colStaffID = new TableColumn<>("Mã nhân viên");
        colStaffID.setCellValueFactory((p) -> {
            Order order = p.getValue();
            String staff = order.getStaffID();
            return new ReadOnlyObjectWrapper<>(staff);
        });

        tvOrder.getColumns().addAll(colOrderID, colOrderDate, colTotal, colStatus, colCustomerID, colStaffID);
        layout.getChildren().add(tvOrder);
        return layout;
    }

    public static void loadOrder() {
        dataOrder.clear();
        try {
            Connection conn = MySQLConnection.getConnection();
            String sql = "SELECT * FROM Orders";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                dataOrder.add(new Order(
                    rs.getString("orderID"),
                    rs.getString("orderDate"),
                    rs.getFloat("total"),
                    rs.getString("status"),
                    rs.getString("customerID"),
                    rs.getString("staffID")
                ));
            }
            tvOrder.setItems(dataOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showOrderItem() {
        Order order = tvOrder.getSelectionModel().getSelectedItem();
        if (order != null) {
            tfOrderID.setText(order.getOrderID());
            cbStatus.setValue(order.getStatus());
            cbCustomerID.setValue(order.getCustomerID());
            cbStaffID.setValue(order.getStaffID());
            // Không set cbBookID và tfQuantity vì mỗi đơn có thể có nhiều chi tiết, chỉ set khi cần
        }
    }

    private static void saveOrder() {
        Alert thongbao = new Alert(Alert.AlertType.INFORMATION);
        thongbao.setTitle("Lưu đơn hàng!!!");
        try {
            Connection conn = MySQLConnection.getConnection();
            if (conn != null) {
                if (tfOrderID.getText().isEmpty() || cbStatus.getValue() == null || cbCustomerID.getValue() == null || cbStaffID.getValue() == null || cbBookID.getValue() == null || tfQuantity.getText().isEmpty()) {
                    thongbao.setContentText("Vui lòng nhập đầy đủ thông tin!");
                    thongbao.show();
                    return;
                }
                int quantity = Integer.parseInt(tfQuantity.getText());
                // Lấy đơn giá sách
                float unitPrice = 0;
                String sqlBook = "SELECT price FROM Books WHERE bookID = ?";
                PreparedStatement psBook = conn.prepareStatement(sqlBook);
                psBook.setString(1, cbBookID.getValue());
                ResultSet rsBook = psBook.executeQuery();
                if (rsBook.next()) {
                    unitPrice = rsBook.getFloat("price");
                }
                rsBook.close();
                psBook.close();
                float total = unitPrice * quantity;
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = now.format(formatter);
                // Lưu vào Orders
                String sqlOrder = "INSERT INTO Orders(orderID, orderDate, total, status, customerID, staffID) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement psOrder = conn.prepareStatement(sqlOrder);
                psOrder.setString(1, tfOrderID.getText());
                psOrder.setString(2, currentDateTime);
                psOrder.setFloat(3, total);
                psOrder.setString(4, cbStatus.getValue());
                psOrder.setString(5, cbCustomerID.getValue());
                psOrder.setString(6, cbStaffID.getValue());
                int kqOrder = psOrder.executeUpdate();
                // Lưu vào OrderDetails
                String sqlDetail = "INSERT INTO OrderDetails(orderID, bookID, quantity, unitPrice) VALUES (?, ?, ?, ?)";
                PreparedStatement psDetail = conn.prepareStatement(sqlDetail);
                psDetail.setString(1, tfOrderID.getText());
                psDetail.setString(2, cbBookID.getValue());
                psDetail.setInt(3, quantity);
                psDetail.setFloat(4, unitPrice);
                int kqDetail = psDetail.executeUpdate();
                if (kqOrder > 0 && kqDetail > 0) {
                    thongbao.setContentText("Lưu đơn hàng thành công!");
                    thongbao.show();
                    dataOrder.clear();
                    loadOrder();
                    loadOrderDetail();
                } else {
                    thongbao.setContentText("Lưu đơn hàng thất bại!");
                    thongbao.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void editOrder() {
        try {
            Connection conn = MySQLConnection.getConnection();
            String sql = "UPDATE Orders SET orderDate=?, total=?, status=?, customerID=?, staffID=? WHERE orderID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfOrderDate.getText());
            ps.setFloat(2, Float.parseFloat(tfTotal.getText()));
            ps.setString(3, cbStatus.getValue());
            ps.setString(4, cbCustomerID.getValue());
            ps.setString(5, cbStaffID.getValue());
            ps.setString(6, tfOrderID.getText());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Sửa đơn hàng thành công!");
                a.show();
                dataOrder.clear();
                loadOrder();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void deleteOrder() {
        try {
            Connection conn = MySQLConnection.getConnection();
            String sql = "DELETE FROM Orders WHERE orderID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, tfOrderID.getText());
            int kq = ps.executeUpdate();
            if (kq > 0) {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Xoá đơn hàng thành công!");
                a.show();
                dataOrder.clear();
                loadOrder();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void clearOrderForm() {
        tfOrderID.clear();
        cbStatus.setValue(null);
        cbCustomerID.setValue(null);
        cbStaffID.setValue(null);
        cbBookID.setValue(null);
        tfQuantity.clear();
    }

    public static BorderPane createOrderContent() {
        BorderPane borderpane = new BorderPane();
        borderpane.setPadding(new Insets(30));
        VBox vbox = new VBox(20);
        vbox.getChildren().addAll(createOrderForm(), showTableOrder(), createOrderDetailTable());
        borderpane.setCenter(vbox);
        btnAddOrder.setOnAction(e -> saveOrder());
        btnDeleteOrder.setOnAction(e -> deleteOrder());
        btnEditOrder.setOnAction(e -> editOrder());
        btnClearOrder.setOnAction(e -> clearOrderForm());
        tvOrder.setOnMouseClicked(e -> showOrderItem());
        tvOrder.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) loadOrderDetail(newSel.getOrderID());
            else dataOrderDetail.clear();
        });
        loadOrder();
        loadOrderDetail();
        return borderpane;
    }

    private static TableView<OrderDetailRow> createOrderDetailTable() {
        tvOrderDetail = new TableView<>();
        tvOrderDetail.setPrefHeight(200);
        TableColumn<OrderDetailRow, String> colOrderID = new TableColumn<>("Mã hóa đơn");
        colOrderID.setCellValueFactory((p) -> {
            OrderDetailRow d = p.getValue();
            String id = d.getOrderID();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(id);
        });
        TableColumn<OrderDetailRow, String> colBookID = new TableColumn<>("Mã sách");
        colBookID.setCellValueFactory((p) -> {
            OrderDetailRow d = p.getValue();
            String book = d.getBookID();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(book);
        });
        TableColumn<OrderDetailRow, Integer> colQuantity = new TableColumn<>("Số lượng");
        colQuantity.setCellValueFactory((p) -> {
            OrderDetailRow d = p.getValue();
            int q = d.getQuantity();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(q);
        });
        TableColumn<OrderDetailRow, Float> colUnitPrice = new TableColumn<>("Đơn giá");
        colUnitPrice.setCellValueFactory((p) -> {
            OrderDetailRow d = p.getValue();
            float up = d.getUnitPrice();
            return new javafx.beans.property.ReadOnlyObjectWrapper<>(up);
        });
        tvOrderDetail.getColumns().addAll(colOrderID, colBookID, colQuantity, colUnitPrice);
        tvOrderDetail.setItems(dataOrderDetail);
        return tvOrderDetail;
    }

    private static void loadOrderDetail(String orderID) {
        dataOrderDetail.clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT orderID, bookID, quantity, unitPrice FROM OrderDetails WHERE orderID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, orderID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String oid = rs.getString("orderID");
                String bid = rs.getString("bookID");
                int q = rs.getInt("quantity");
                float up = rs.getFloat("unitPrice");
                dataOrderDetail.add(new OrderDetailRow(oid, bid, q, up));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadOrderDetail() {
        dataOrderDetail.clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT orderID, bookID, quantity, unitPrice FROM OrderDetails";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String oid = rs.getString("orderID");
                String bid = rs.getString("bookID");
                int q = rs.getInt("quantity");
                float up = rs.getFloat("unitPrice");
                dataOrderDetail.add(new OrderDetailRow(oid, bid, q, up));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadCustomerIDs() {
        cbCustomerID.getItems().clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT customerID FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                cbCustomerID.getItems().add(rs.getString("customerID"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void loadStaffIDs() {
        cbStaffID.getItems().clear();
        try (Connection conn = MySQLConnection.getConnection()) {
            String sql = "SELECT staffID FROM Staffs";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                cbStaffID.getItems().add(rs.getString("staffID"));
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

    public static class OrderDetailRow {
        private final SimpleStringProperty orderID;
        private final SimpleStringProperty bookID;
        private final SimpleIntegerProperty quantity;
        private final SimpleFloatProperty unitPrice;
        public OrderDetailRow(String orderID, String bookID, int quantity, float unitPrice) {
            this.orderID = new SimpleStringProperty(orderID);
            this.bookID = new SimpleStringProperty(bookID);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.unitPrice = new SimpleFloatProperty(unitPrice);
        }
        public String getOrderID() { return orderID.get(); }
        public String getBookID() { return bookID.get(); }
        public int getQuantity() { return quantity.get(); }
        public float getUnitPrice() { return unitPrice.get(); }
        public SimpleStringProperty orderIDProperty() { return orderID; }
        public SimpleStringProperty bookIDProperty() { return bookID; }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        public SimpleFloatProperty unitPriceProperty() { return unitPrice; }
    }

    public static void main(String[] args) {
        launch();
    }
} 