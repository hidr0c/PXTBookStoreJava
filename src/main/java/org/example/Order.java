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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Order extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane orderContent = createOrderContent();
        Scene scene = new Scene(orderContent, 800, 600);
        primaryStage.setTitle("Order Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Thêm các TextField ở class-level
    private TextField tfOrderId = new TextField();
    private TextField tfOrderDate = new TextField();
    private TextField tfTotal = new TextField();
    private TextField tfStatus = new TextField();
    private TextField tfCustomerId = new TextField();
    private TextField tfStaffId = new TextField();

    public BorderPane createOrderContent() {
        BorderPane ordersContent = new BorderPane();

        VBox topContent = new VBox();
        Label titleLabel = new Label("Order Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button createNewButton = new Button("Create New Order");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(e -> showAddOrderDialog());

        // Thêm nút Sửa và Xóa
        Button editOrderBtn = new Button("Sửa Order");
        Button deleteOrderBtn = new Button("Xóa Order");
        editOrderBtn.setDisable(true);
        deleteOrderBtn.setDisable(true);
        HBox orderBtnBox = new HBox(10, createNewButton, editOrderBtn, deleteOrderBtn);
        orderBtnBox.setPadding(new Insets(0, 0, 10, 0));

        topContent.getChildren().addAll(titleLabel, orderBtnBox);
        topContent.setSpacing(10);
        topContent.setPadding(new Insets(10));
        ordersContent.setTop(topContent);

        // Orders Table
        TableView<OrderRow> ordersTable = new TableView<>();
        ordersTable.setPrefHeight(200);
        TableColumn<OrderRow, String> orderIdColumn = new TableColumn<>("Order ID");
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        TableColumn<OrderRow, String> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        TableColumn<OrderRow, String> totalAmountColumn = new TableColumn<>("Total Amount");
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
        TableColumn<OrderRow, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        TableColumn<OrderRow, String> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        ordersTable.getColumns().setAll(orderIdColumn, orderDateColumn, totalAmountColumn, statusColumn, customerIdColumn);
        // Thêm cột "Xem chi tiết" bên phải
        TableColumn<OrderRow, Void> detailCol = new TableColumn<>("Xem chi tiết");
        detailCol.setCellFactory(col -> new TableCell<OrderRow, Void>() {
            private final Hyperlink link = new Hyperlink("Xem chi tiết");
            {
                link.setOnAction(e -> {
                    OrderRow order = getTableView().getItems().get(getIndex());
                    if (order != null) {
                        showOrderDetailPopup(order.orderIdProperty().get());
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : link);
            }
        });
        ordersTable.getColumns().add(detailCol);
        ordersTable.setItems(loadOrdersFromMongo());

        // Enable/disable edit & delete buttons
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editOrderBtn.setDisable(!selected);
            deleteOrderBtn.setDisable(!selected);
        });

        // Xử lý nút Xóa
        deleteOrderBtn.setOnAction(e -> {
            OrderRow selected = ordersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận xóa");
                alert.setHeaderText(null);
                alert.setContentText("Bạn xác nhận xóa order?");
                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    deleteOrder(selected.orderIdProperty().get());
                    ordersTable.setItems(loadOrdersFromMongo());
                }
            }
        });

        // Xử lý nút Sửa
        editOrderBtn.setOnAction(e -> {
            OrderRow selected = ordersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditOrderDialog(selected);
                ordersTable.setItems(loadOrdersFromMongo());
            }
        });

        ordersContent.setCenter(ordersTable);

        // ContextMenu cho Order Table
        ContextMenu orderContextMenu = new ContextMenu();
        MenuItem viewDetailItem = new MenuItem("Xem chi tiết");
        orderContextMenu.getItems().add(viewDetailItem);
        ordersTable.setRowFactory(tv -> {
            TableRow<OrderRow> row = new TableRow<>();
            row.setOnContextMenuRequested(event -> {
                if (!row.isEmpty()) {
                    orderContextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            viewDetailItem.setOnAction(e -> {
                OrderRow selectedOrder = row.getItem();
                if (selectedOrder != null) {
                    showOrderDetailPopup(selectedOrder.orderIdProperty().get());
                }
            });
            return row;
        });
        // CRUD buttons for Orders Table
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            // editOrderBtn.setDisable(!selected); // This line is removed
            // deleteOrderBtn.setDisable(!selected); // This line is removed
        });
        // ordersContent.setCenter(ordersTable); // This line is moved up

        // Customers Table
        TableView<CustomerRow> customersTable = new TableView<>();
        customersTable.setPrefHeight(150);
        TableColumn<CustomerRow, String> customerIdCustomerColumn = new TableColumn<>("Customer ID");
        customerIdCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        TableColumn<CustomerRow, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        TableColumn<CustomerRow, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        TableColumn<CustomerRow, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        TableColumn<CustomerRow, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        TableColumn<CustomerRow, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        TableColumn<CustomerRow, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        TableColumn<CustomerRow, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty());
        TableColumn<CustomerRow, String> spendingColumn = new TableColumn<>("Spending");
        spendingColumn.setCellValueFactory(cellData -> cellData.getValue().spendingProperty());
        customersTable.getColumns().setAll(customerIdCustomerColumn, emailColumn, passwordColumn, roleColumn, fullNameColumn, addressColumn, phoneNumberColumn, rankColumn, spendingColumn);
        customersTable.setItems(loadCustomersFromMongo());
        // CRUD buttons for Customers Table
        Button addCustomerBtn = new Button("Thêm Customer");
        Button editCustomerBtn = new Button("Sửa Customer");
        Button deleteCustomerBtn = new Button("Xóa Customer");
        editCustomerBtn.setDisable(true);
        deleteCustomerBtn.setDisable(true);
        HBox customerBtnBox = new HBox(10, addCustomerBtn, editCustomerBtn, deleteCustomerBtn);
        customerBtnBox.setPadding(new Insets(0, 0, 10, 0));
        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editCustomerBtn.setDisable(!selected);
            deleteCustomerBtn.setDisable(!selected);
        });

        // OrderDetails Table
        TableView<OrderDetailRow> orderDetailsTable = new TableView<>();
        orderDetailsTable.setPrefHeight(150);
        TableColumn<OrderDetailRow, String> orderIdOrderDetailsColumn = new TableColumn<>("Order ID");
        orderIdOrderDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        TableColumn<OrderDetailRow, String> bookIdOrderDetailsColumn = new TableColumn<>("Book ID");
        bookIdOrderDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        TableColumn<OrderDetailRow, String> titleOrderDetailsColumn = new TableColumn<>("Book Title");
        titleOrderDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<OrderDetailRow, String> unitPriceOrderDetailsColumn = new TableColumn<>("Unit Price");
        unitPriceOrderDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());
        TableColumn<OrderDetailRow, String> quantityOrderDetailsColumn = new TableColumn<>("Quantity");
        quantityOrderDetailsColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        orderDetailsTable.getColumns().setAll(orderIdOrderDetailsColumn, bookIdOrderDetailsColumn, titleOrderDetailsColumn, unitPriceOrderDetailsColumn, quantityOrderDetailsColumn);
        orderDetailsTable.setItems(loadOrderDetailsFromMongo());
        // CRUD buttons for OrderDetails Table
        Button addOrderDetailBtn = new Button("Thêm OrderDetail");
        Button editOrderDetailBtn = new Button("Sửa OrderDetail");
        Button deleteOrderDetailBtn = new Button("Xóa OrderDetail");
        editOrderDetailBtn.setDisable(true);
        deleteOrderDetailBtn.setDisable(true);
        HBox orderDetailBtnBox = new HBox(10, addOrderDetailBtn, editOrderDetailBtn, deleteOrderDetailBtn);
        orderDetailBtnBox.setPadding(new Insets(0, 0, 10, 0));
        orderDetailsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editOrderDetailBtn.setDisable(!selected);
            deleteOrderDetailBtn.setDisable(!selected);
        });

        // Nếu muốn có thêm bảng khách hàng hoặc chi tiết, có thể add vào setBottom hoặc tab khác
        return ordersContent;
    }

    public static void main(String[] args) {
        launch(args);
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
    // Model for Customers Table
    public static class CustomerRow {
        private SimpleStringProperty customerId, email, password, role, fullName, address, phoneNumber, rank, spending;
        public CustomerRow(String customerId, String email, String password, String role, String fullName, String address, String phoneNumber, String rank, String spending) {
            this.customerId = new SimpleStringProperty(customerId);
            this.email = new SimpleStringProperty(email);
            this.password = new SimpleStringProperty(password);
            this.role = new SimpleStringProperty(role);
            this.fullName = new SimpleStringProperty(fullName);
            this.address = new SimpleStringProperty(address);
            this.phoneNumber = new SimpleStringProperty(phoneNumber);
            this.rank = new SimpleStringProperty(rank);
            this.spending = new SimpleStringProperty(spending);
        }
        public SimpleStringProperty customerIdProperty() { return customerId; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty passwordProperty() { return password; }
        public SimpleStringProperty roleProperty() { return role; }
        public SimpleStringProperty fullNameProperty() { return fullName; }
        public SimpleStringProperty addressProperty() { return address; }
        public SimpleStringProperty phoneNumberProperty() { return phoneNumber; }
        public SimpleStringProperty rankProperty() { return rank; }
        public SimpleStringProperty spendingProperty() { return spending; }
    }
    // Sửa model OrderDetailRow để có thêm trường title
    public static class OrderDetailRow {
        private SimpleStringProperty orderId, bookId, title, unitPrice, quantity;
        public OrderDetailRow(String orderId, String bookId, String title, String unitPrice, String quantity) {
            this.orderId = new SimpleStringProperty(orderId);
            this.bookId = new SimpleStringProperty(bookId);
            this.title = new SimpleStringProperty(title);
            this.unitPrice = new SimpleStringProperty(unitPrice);
            this.quantity = new SimpleStringProperty(quantity);
        }
        public SimpleStringProperty orderIdProperty() { return orderId; }
        public SimpleStringProperty bookIdProperty() { return bookId; }
        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty unitPriceProperty() { return unitPrice; }
        public SimpleStringProperty quantityProperty() { return quantity; }
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
    // Load Customers from MongoDB
    private ObservableList<CustomerRow> loadCustomersFromMongo() {
        ObservableList<CustomerRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> col = db.getCollection("Customers");
            try (MongoCursor<Document> cursor = col.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String customerId = doc.getString("customerId");
                    String email = doc.containsKey("email") ? doc.getString("email") : "";
                    String password = doc.containsKey("password") ? doc.getString("password") : "";
                    String role = doc.containsKey("role") ? doc.getString("role") : "";
                    String fullName = doc.containsKey("fullName") ? doc.getString("fullName") : "";
                    String address = doc.containsKey("address") ? doc.getString("address") : "";
                    String phoneNumber = doc.containsKey("phoneNumber") ? doc.getString("phoneNumber") : "";
                    String rank = doc.containsKey("rank") ? doc.getString("rank") : "";
                    String spending = doc.containsKey("spending") ? doc.get("spending").toString() : "";
                    list.add(new CustomerRow(customerId, email, password, role, fullName, address, phoneNumber, rank, spending));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading customers from MongoDB: " + e.getMessage());
        }
        return list;
    }
    // Sửa hàm loadOrderDetailsFromMongo để join thêm Book.title
    private ObservableList<OrderDetailRow> loadOrderDetailsFromMongo() {
        ObservableList<OrderDetailRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> orderDetailsCol = db.getCollection("OrderDetails");
            MongoCollection<Document> booksCol = db.getCollection("Books");
            // Tạo map bookId -> title
            java.util.Map<String, String> bookTitleMap = new java.util.HashMap<>();
            try (MongoCursor<Document> cursor = booksCol.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String bookId = doc.getString("bookId");
                    String title = doc.containsKey("title") ? doc.getString("title") : "";
                    bookTitleMap.put(bookId, title);
                }
            }
            // Load OrderDetails và join title
            try (MongoCursor<Document> cursor = orderDetailsCol.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String orderId = doc.getString("orderId");
                    String bookId = doc.containsKey("bookId") ? doc.getString("bookId") : "";
                    String title = bookTitleMap.getOrDefault(bookId, "");
                    String unitPrice = doc.containsKey("unitPrice") ? doc.get("unitPrice").toString() : "";
                    String quantity = doc.containsKey("quantity") ? doc.get("quantity").toString() : "";
                    list.add(new OrderDetailRow(orderId, bookId, title, unitPrice, quantity));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading order details from MongoDB: " + e.getMessage());
        }
        return list;
    }

    // Hàm popup hiển thị chi tiết OrderDetail theo orderId
    private void showOrderDetailPopup(String orderId) {
        Stage stage = new Stage();
        stage.setTitle("Chi tiết hóa đơn: " + orderId);
        TableView<OrderDetailRow> detailTable = new TableView<>();
        TableColumn<OrderDetailRow, String> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(cell -> cell.getValue().orderIdProperty());
        TableColumn<OrderDetailRow, String> bookIdCol = new TableColumn<>("Book ID");
        bookIdCol.setCellValueFactory(cell -> cell.getValue().bookIdProperty());
        TableColumn<OrderDetailRow, String> titleCol = new TableColumn<>("Book Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());
        TableColumn<OrderDetailRow, String> unitPriceCol = new TableColumn<>("Unit Price");
        unitPriceCol.setCellValueFactory(cell -> cell.getValue().unitPriceProperty());
        TableColumn<OrderDetailRow, String> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(cell -> cell.getValue().quantityProperty());
        detailTable.getColumns().addAll(orderIdCol, bookIdCol, titleCol, unitPriceCol, quantityCol);
        // Lọc dữ liệu theo orderId
        ObservableList<OrderDetailRow> allDetails = loadOrderDetailsFromMongo();
        ObservableList<OrderDetailRow> filtered = FXCollections.observableArrayList();
        for (OrderDetailRow row : allDetails) {
            if (row.orderIdProperty().get().equals(orderId)) {
                filtered.add(row);
            }
        }
        detailTable.setItems(filtered);
        Button closeBtn = new Button("Đóng");
        closeBtn.setOnAction(e -> stage.close());
        VBox vbox = new VBox(10, detailTable, closeBtn);
        vbox.setPadding(new Insets(10));
        stage.setScene(new Scene(vbox, 600, 400));
        stage.showAndWait();
    }

    // Dialog thêm Order và OrderDetail
    private void showAddOrderDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Thêm hóa đơn mới");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        // Order fields
        TextField tfOrderId = new TextField();
        TextField tfTotal = new TextField();
        TextField tfStatus = new TextField();

        // Customer ID ComboBox
        javafx.scene.control.ComboBox<String> cbCustomerId = new javafx.scene.control.ComboBox<>();
        cbCustomerId.setEditable(false);
        cbCustomerId.setPromptText("Chọn Customer ID");
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> customersCol = db.getCollection("Customers");
            MongoCursor<Document> cursor = customersCol.find().iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String customerId = doc.getString("customerId");
                if (customerId != null) cbCustomerId.getItems().add(customerId);
            }
        } catch (Exception e) {
            System.out.println("Error loading Customer IDs: " + e.getMessage());
        }

        // Staff ID ComboBox
        javafx.scene.control.ComboBox<String> cbStaffId = new javafx.scene.control.ComboBox<>();
        cbStaffId.setEditable(false);
        cbStaffId.setPromptText("Chọn Staff ID");
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> staffCol = db.getCollection("Staff");
            MongoCursor<Document> cursor = staffCol.find().iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String staffId = doc.getString("staffId");
                if (staffId != null) cbStaffId.getItems().add(staffId);
            }
        } catch (Exception e) {
            System.out.println("Error loading Staff IDs: " + e.getMessage());
        }

        // Book ID ComboBox
        javafx.scene.control.ComboBox<String> cbBookId = new javafx.scene.control.ComboBox<>();
        cbBookId.setEditable(false);
        cbBookId.setPromptText("Chọn Book ID");
        java.util.Map<String, String> bookPriceMap = new java.util.HashMap<>();
        // Load Book IDs and prices from MongoDB
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> booksCol = db.getCollection("Books");
            MongoCursor<Document> cursor = booksCol.find().iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String bookId = doc.getString("bookId");
                String price = doc.containsKey("price") ? doc.get("price").toString() : "";
                if (bookId != null) {
                    cbBookId.getItems().add(bookId);
                    bookPriceMap.put(bookId, price);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading Book IDs: " + e.getMessage());
        }

        TextField tfQuantity = new TextField();
        TextField tfUnitPrice = new TextField();
        tfUnitPrice.setEditable(false); // Không cho nhập tay
        tfUnitPrice.setPromptText("Tự động theo Book ID");

        // Khi chọn Book ID thì tự động điền giá
        cbBookId.setOnAction(e -> {
            String selectedBookId = cbBookId.getValue();
            String price = bookPriceMap.getOrDefault(selectedBookId, "");
            tfUnitPrice.setText(price);
        });

        vbox.getChildren().addAll(
            new Label("Order ID:"), tfOrderId,
            new Label("Total Amount:"), tfTotal,
            new Label("Status:"), tfStatus,
            new Label("Customer ID:"), cbCustomerId,
            new Label("Staff ID:"), cbStaffId,
            new Label("Book ID:"), cbBookId,
            new Label("Quantity:"), tfQuantity,
            new Label("Unit Price:"), tfUnitPrice
        );

        Button btnSave = new Button("Lưu");
        btnSave.setOnAction(ev -> {
            // Lấy ngày hiện tại định dạng yyyy-MM-dd
            String orderDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            addOrderAndDetail(
                tfOrderId.getText(), orderDate, tfTotal.getText(), tfStatus.getText(),
                cbCustomerId.getValue(), cbStaffId.getValue(),
                cbBookId.getValue(), tfQuantity.getText(), tfUnitPrice.getText()
            );
            dialog.close();
        });
        vbox.getChildren().add(btnSave);

        // Đặt VBox vào ScrollPane
        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(vbox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(500);
        Scene scene = new Scene(scrollPane, 350, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void addOrderAndDetail(String orderId, String orderDate, String total, String status,
                                   String customerId, String staffId,
                                   String bookId, String quantity, String unitPrice) {
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> ordersCol = db.getCollection("Orders");
            Document orderDoc = new Document("orderId", orderId)
                .append("orderDate", orderDate)
                .append("totalAmount", total)
                .append("status", status)
                .append("customerId", customerId)
                .append("staffId", staffId);
            ordersCol.insertOne(orderDoc);

            MongoCollection<Document> detailsCol = db.getCollection("OrderDetails");
            Document detailDoc = new Document("orderId", orderId)
                .append("bookId", bookId)
                .append("quantity", Integer.parseInt(quantity))
                .append("unitPrice", Double.parseDouble(unitPrice));
            detailsCol.insertOne(detailDoc);
            // Reload lại bảng Order nếu cần
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void deleteOrder(String orderId) {
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> ordersCol = db.getCollection("Orders");
            MongoCollection<Document> detailsCol = db.getCollection("OrderDetails");
            ordersCol.deleteOne(new Document("orderId", orderId));
            detailsCol.deleteMany(new Document("orderId", orderId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEditOrderDialog(OrderRow order) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Sửa hóa đơn: " + order.orderIdProperty().get());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        TextField tfOrderId = new TextField(order.orderIdProperty().get());
        tfOrderId.setEditable(false);
        TextField tfTotal = new TextField(order.totalAmountProperty().get());
        TextField tfStatus = new TextField(order.statusProperty().get());
        // Customer ID ComboBox
        javafx.scene.control.ComboBox<String> cbCustomerId = new javafx.scene.control.ComboBox<>();
        cbCustomerId.setEditable(false);
        cbCustomerId.setPromptText("Chọn Customer ID");
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> customersCol = db.getCollection("Customers");
            MongoCursor<Document> cursor = customersCol.find().iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String customerId = doc.getString("customerId");
                if (customerId != null) cbCustomerId.getItems().add(customerId);
            }
        } catch (Exception e) {
            System.out.println("Error loading Customer IDs: " + e.getMessage());
        }
        cbCustomerId.setValue(order.customerIdProperty().get());

        // Staff ID ComboBox
        javafx.scene.control.ComboBox<String> cbStaffId = new javafx.scene.control.ComboBox<>();
        cbStaffId.setEditable(false);
        cbStaffId.setPromptText("Chọn Staff ID");
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> staffCol = db.getCollection("Staff");
            MongoCursor<Document> cursor = staffCol.find().iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String staffId = doc.getString("staffId");
                if (staffId != null) cbStaffId.getItems().add(staffId);
            }
        } catch (Exception e) {
            System.out.println("Error loading Staff IDs: " + e.getMessage());
        }
        // Không có staffId trong OrderRow nên để trống

        vbox.getChildren().addAll(
            new Label("Order ID:"), tfOrderId,
            new Label("Total Amount:"), tfTotal,
            new Label("Status:"), tfStatus,
            new Label("Customer ID:"), cbCustomerId,
            new Label("Staff ID:"), cbStaffId
        );

        Button btnSave = new Button("Lưu");
        btnSave.setOnAction(ev -> {
            updateOrder(
                tfOrderId.getText(),
                tfTotal.getText(),
                tfStatus.getText(),
                cbCustomerId.getValue(),
                cbStaffId.getValue()
            );
            dialog.close();
        });
        vbox.getChildren().add(btnSave);

        Scene scene = new Scene(vbox, 350, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void updateOrder(String orderId, String total, String status, String customerId, String staffId) {
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> ordersCol = db.getCollection("Orders");
            Document update = new Document("totalAmount", total)
                .append("status", status)
                .append("customerId", customerId)
                .append("staffId", staffId);
            ordersCol.updateOne(new Document("orderId", orderId), new Document("$set", update));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 