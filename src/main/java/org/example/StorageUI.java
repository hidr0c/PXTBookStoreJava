package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StorageUI extends Application {

    private ObservableList<Inventory> inventoryData = FXCollections.observableArrayList(
            new Inventory("INV001", "BOOK001", "Harry Potter and the Philosopher's Stone", 58, "In Stock"),
            new Inventory("INV002", "BOOK002", "The Shining", 32, "In Stock"),
            new Inventory("INV003", "BOOK003", "Mắt Biếc", 15, "Low Stock"),
            new Inventory("INV004", "BOOK004", "The Da Vinci Code", 40, "In Stock"),
            new Inventory("INV005", "BOOK005", "Digital Fortress", 0, "Out of Stock"));

    private ObservableList<Order> orderData = FXCollections.observableArrayList(
            new Order("ORD001", "2023-06-15", 45.98, "Completed", "CUST001"),
            new Order("ORD002", "2023-06-18", 25.98, "Completed", "CUST003"),
            new Order("ORD003", "2023-06-20", 14.50, "Shipped", "CUST002"),
            new Order("ORD004", "2023-06-25", 29.98, "Processing", "CUST004"),
            new Order("ORD005", "2023-06-28", 11.99, "Pending", "CUST005"));

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Quản lý kho hàng");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Search and buttons section
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm sản phẩm hoặc đơn hàng...");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;");
        Button createNewButton = new Button("Thêm vào kho");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewStorageWindow());

        actionBar.getChildren().addAll(searchField, searchButton, createNewButton);

        // Stats overview
        HBox statsBar = new HBox(20);
        statsBar.setPadding(new Insets(10));
        statsBar.setAlignment(Pos.CENTER);

        VBox totalItemsStats = createStatBox("Tổng sản phẩm", "145");
        VBox inStockStats = createStatBox("Còn hàng", "130");
        VBox lowStockStats = createStatBox("Sắp hết", "15");
        VBox outOfStockStats = createStatBox("Hết hàng", "5");

        statsBar.getChildren().addAll(totalItemsStats, inStockStats, lowStockStats, outOfStockStats);

        topSection.getChildren().addAll(titleLabel, actionBar, statsBar);
        root.setTop(topSection);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Inventory tab with table
        Tab inventoryTab = new Tab("Kho hàng");
        VBox inventoryBox = new VBox(10);
        inventoryBox.setPadding(new Insets(10));

        HBox inventoryHeader = new HBox(10);
        Label inventoryLabel = new Label("Quản lý kho hàng");
        inventoryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button exportInventoryBtn = new Button("Xuất báo cáo kho");
        exportInventoryBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        inventoryHeader.getChildren().addAll(inventoryLabel, new Pane(), exportInventoryBtn);
        HBox.setHgrow(new Pane(), Priority.ALWAYS);

        // Inventory table
        TableView<Inventory> inventoryTable = new TableView<>();
        inventoryTable.setPrefHeight(250);
        inventoryTable.setItems(inventoryData);

        TableColumn<Inventory, String> inventoryIdColumn = new TableColumn<>("Mã kho");
        inventoryIdColumn.setCellValueFactory(cellData -> cellData.getValue().inventoryIdProperty());
        inventoryIdColumn.setPrefWidth(100);

        TableColumn<Inventory, String> bookIdColumn = new TableColumn<>("Mã sách");
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        bookIdColumn.setPrefWidth(80);

        TableColumn<Inventory, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        titleColumn.setPrefWidth(200);

        TableColumn<Inventory, Number> quantityColumn = new TableColumn<>("Số lượng");
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        quantityColumn.setPrefWidth(80);

        TableColumn<Inventory, String> statusColumn = new TableColumn<>("Trạng thái");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusColumn.setPrefWidth(100);
        statusColumn.setCellFactory(column -> {
            return new TableCell<Inventory, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        String displayText = item;
                        if ("Out of Stock".equals(item)) {
                            displayText = "Hết hàng";
                            setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                        } else if ("Low Stock".equals(item)) {
                            displayText = "Sắp hết";
                            setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                        } else if ("In Stock".equals(item)) {
                            displayText = "Còn hàng";
                            setStyle("-fx-text-fill: #28a745;");
                        }
                        setText(displayText);
                    }
                }
            };
        });
        TableColumn<Inventory, Void> actionColumn = new TableColumn<>("Thao tác");
        actionColumn.setPrefWidth(180);
        actionColumn.setCellFactory(param -> new TableCell<Inventory, Void>() {
            private final Button editBtn = new Button("Sửa");
            private final Button adjustBtn = new Button("Điều chỉnh");
            private final Button historyBtn = new Button("Lịch sử");

            {
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                adjustBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");
                historyBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Inventory data = getTableView().getItems().get(getIndex());
                    // Edit functionality here
                    System.out.println("Edit button clicked for: " + data.getTitle());
                });

                adjustBtn.setOnAction(event -> {
                    Inventory data = getTableView().getItems().get(getIndex());
                    // Adjust stock functionality here
                    System.out.println("Adjust stock button clicked for: " + data.getTitle());
                });

                historyBtn.setOnAction(event -> {
                    Inventory data = getTableView().getItems().get(getIndex());
                    // View history functionality
                    System.out.println("History button clicked for: " + data.getTitle());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(5);
                    pane.setAlignment(Pos.CENTER);
                    pane.getChildren().addAll(editBtn, adjustBtn, historyBtn);
                    setGraphic(pane);
                }
            }
        }); // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        inventoryTable.getColumns().add(inventoryIdColumn);
        inventoryTable.getColumns().add(bookIdColumn);
        inventoryTable.getColumns().add(titleColumn);
        inventoryTable.getColumns().add(quantityColumn);
        inventoryTable.getColumns().add(statusColumn);
        inventoryTable.getColumns().add(actionColumn);

        inventoryBox.getChildren().addAll(inventoryHeader, inventoryTable);
        inventoryTab.setContent(inventoryBox);

        // Orders tab with table
        Tab ordersTab = new Tab("Đơn hàng");
        VBox ordersBox = new VBox(10);
        ordersBox.setPadding(new Insets(10));

        Label ordersLabel = new Label("Lịch sử đơn hàng");
        ordersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Orders table
        TableView<Order> ordersTable = new TableView<>();
        ordersTable.setPrefHeight(250);
        ordersTable.setItems(orderData);
        TableColumn<Order, String> orderIdColumn = new TableColumn<>("Mã đơn hàng");
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        orderIdColumn.setPrefWidth(80);

        TableColumn<Order, String> orderDateColumn = new TableColumn<>("Ngày đặt");
        orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        orderDateColumn.setPrefWidth(100);

        TableColumn<Order, Number> totalAmountColumn = new TableColumn<>("Tổng tiền");
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty());
        totalAmountColumn.setPrefWidth(100);
        totalAmountColumn.setCellFactory(column -> {
            return new TableCell<Order, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("%.0fđ", item.doubleValue() * 23000)); // Chuyển đổi sang VNĐ
                    }
                }
            };
        });

        TableColumn<Order, String> orderStatusColumn = new TableColumn<>("Trạng thái");
        orderStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        orderStatusColumn.setPrefWidth(100);
        orderStatusColumn.setCellFactory(column -> {
            return new TableCell<Order, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        String displayText = item;
                        if ("Completed".equals(item)) {
                            displayText = "Hoàn thành";
                            setStyle("-fx-text-fill: #28a745;");
                        } else if ("Shipped".equals(item)) {
                            displayText = "Đang giao";
                            setStyle("-fx-text-fill: #007bff;");
                        } else if ("Processing".equals(item)) {
                            displayText = "Đang xử lý";
                            setStyle("-fx-text-fill: #fd7e14;");
                        } else if ("Pending".equals(item)) {
                            displayText = "Chờ xử lý";
                            setStyle("-fx-text-fill: #6c757d;");
                        }
                        setText(displayText);
                    }
                }
            };
        });
        TableColumn<Order, String> customerIdColumn = new TableColumn<>("Mã khách hàng");
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        customerIdColumn.setPrefWidth(100);

        TableColumn<Order, Void> orderActionColumn = new TableColumn<>("Thao tác");
        orderActionColumn.setPrefWidth(180);
        orderActionColumn.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button viewBtn = new Button("Xem");
            private final Button updateBtn = new Button("Cập nhật");

            {
                viewBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                updateBtn.setStyle("-fx-background-color: #fd7e14; -fx-text-fill: white;");

                viewBtn.setOnAction(event -> {
                    Order data = getTableView().getItems().get(getIndex());
                    // View order details
                    System.out.println("View button clicked for order: " + data.getOrderId());
                });

                updateBtn.setOnAction(event -> {
                    Order data = getTableView().getItems().get(getIndex());
                    // Update order status
                    System.out.println("Update status button clicked for order: " + data.getOrderId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(5);
                    pane.setAlignment(Pos.CENTER);
                    pane.getChildren().addAll(viewBtn, updateBtn);
                    setGraphic(pane);
                }
            }
        }); // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        ordersTable.getColumns().add(orderIdColumn);
        ordersTable.getColumns().add(orderDateColumn);
        ordersTable.getColumns().add(totalAmountColumn);
        ordersTable.getColumns().add(orderStatusColumn);
        ordersTable.getColumns().add(customerIdColumn);
        ordersTable.getColumns().add(orderActionColumn);

        ordersBox.getChildren().addAll(ordersLabel, ordersTable);
        ordersTab.setContent(ordersBox);

        // Stock Alerts tab
        Tab alertsTab = new Tab("Cảnh báo kho");
        VBox alertsBox = createPlaceholderContent("Thông báo và cảnh báo về kho hàng sẽ hiển thị tại đây");
        alertsTab.setContent(alertsBox);

        // Stock Movement tab
        Tab movementTab = new Tab("Di chuyển hàng");
        VBox movementBox = createPlaceholderContent("Lịch sử di chuyển hàng hóa sẽ hiển thị tại đây");
        movementTab.setContent(movementBox);

        // Thêm từng tab riêng lẻ để tránh cảnh báo type safety
        tabPane.getTabs().add(inventoryTab);
        tabPane.getTabs().add(ordersTab);
        tabPane.getTabs().add(alertsTab);
        tabPane.getTabs().add(movementTab);
        root.setCenter(tabPane); // Status bar at the bottom
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #f8f9fa;");
        Label statusLabel = new Label("Sẵn sàng");
        statusBar.getChildren().add(statusLabel);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Quản lý hiệu sách - Kho hàng");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createPlaceholderContent(String message) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label placeholderLabel = new Label(message);
        placeholderLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #999999;");

        box.getChildren().add(placeholderLabel);
        return box;
    }

    private VBox createStatBox(String title, String value) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setStyle(
                "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        box.setPrefWidth(150);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        box.getChildren().addAll(titleLabel, valueLabel);
        return box;
    }

    private void showCreateNewStorageWindow() {
        Stage newWindow = new Stage();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Inventory Tab
        Tab inventoryTab = new Tab("New Inventory");
        VBox inventoryForm = new VBox(10);
        inventoryForm.setPadding(new Insets(20));

        Label inventoryTitle = new Label("Add New Inventory");
        inventoryTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane inventoryGrid = new GridPane();
        inventoryGrid.setHgap(10);
        inventoryGrid.setVgap(10);
        inventoryGrid.setPadding(new Insets(10));

        inventoryGrid.add(new Label("Inventory ID:"), 0, 0);
        TextField inventoryIdField = new TextField();
        inventoryIdField.setPromptText("Auto-generated");
        inventoryIdField.setDisable(true);
        inventoryGrid.add(inventoryIdField, 1, 0);

        inventoryGrid.add(new Label("Book:"), 0, 1);
        ComboBox<String> bookCombo = new ComboBox<>();
        bookCombo.getItems().addAll(
                "Harry Potter and the Philosopher's Stone",
                "The Shining",
                "Mắt Biếc",
                "The Da Vinci Code",
                "Digital Fortress");
        inventoryGrid.add(bookCombo, 1, 1);

        inventoryGrid.add(new Label("Quantity:"), 0, 2);
        TextField quantityField = new TextField();
        quantityField.setText("0");
        inventoryGrid.add(quantityField, 1, 2);

        inventoryGrid.add(new Label("Location:"), 0, 3);
        ComboBox<String> locationCombo = new ComboBox<>();
        locationCombo.getItems().addAll("Main Storage", "Display Area", "Back Room", "Reserve");
        locationCombo.setValue("Main Storage");
        inventoryGrid.add(locationCombo, 1, 3);

        HBox inventoryButtons = new HBox(10);
        Button saveInventoryButton = new Button("Save Inventory");
        saveInventoryButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelInventoryButton = new Button("Cancel");
        inventoryButtons.getChildren().addAll(saveInventoryButton, cancelInventoryButton);
        inventoryButtons.setAlignment(Pos.CENTER_RIGHT);

        inventoryForm.getChildren().addAll(inventoryTitle, inventoryGrid, inventoryButtons);
        inventoryTab.setContent(inventoryForm);

        // Stock Adjustment Tab
        Tab adjustmentTab = new Tab("Stock Adjustment");
        VBox adjustmentForm = new VBox(10);
        adjustmentForm.setPadding(new Insets(20));

        Label adjustmentTitle = new Label("Adjust Stock");
        adjustmentTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane adjustmentGrid = new GridPane();
        adjustmentGrid.setHgap(10);
        adjustmentGrid.setVgap(10);
        adjustmentGrid.setPadding(new Insets(10));

        adjustmentGrid.add(new Label("Book:"), 0, 0);
        ComboBox<String> bookAdjustCombo = new ComboBox<>();
        bookAdjustCombo.getItems().addAll(
                "Harry Potter and the Philosopher's Stone",
                "The Shining",
                "Mắt Biếc",
                "The Da Vinci Code",
                "Digital Fortress");
        adjustmentGrid.add(bookAdjustCombo, 1, 0);

        adjustmentGrid.add(new Label("Current Quantity:"), 0, 1);
        Label currentQuantityLabel = new Label("0");
        currentQuantityLabel.setStyle("-fx-font-weight: bold;");
        adjustmentGrid.add(currentQuantityLabel, 1, 1);

        adjustmentGrid.add(new Label("Adjustment:"), 0, 2);
        HBox adjustmentBox = new HBox(10);
        ComboBox<String> adjustmentTypeCombo = new ComboBox<>();
        adjustmentTypeCombo.getItems().addAll("Add", "Remove");
        adjustmentTypeCombo.setValue("Add");
        TextField adjustmentQuantityField = new TextField();
        adjustmentQuantityField.setPrefWidth(80);
        adjustmentBox.getChildren().addAll(adjustmentTypeCombo, adjustmentQuantityField);
        adjustmentGrid.add(adjustmentBox, 1, 2);

        adjustmentGrid.add(new Label("Reason:"), 0, 3);
        ComboBox<String> reasonCombo = new ComboBox<>();
        reasonCombo.getItems().addAll("New Stock", "Returned Items", "Damage/Loss", "Inventory Correction", "Sale");
        reasonCombo.setValue("New Stock");
        adjustmentGrid.add(reasonCombo, 1, 3);

        adjustmentGrid.add(new Label("Notes:"), 0, 4);
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        adjustmentGrid.add(notesArea, 1, 4);

        HBox adjustmentButtons = new HBox(10);
        Button saveAdjustmentButton = new Button("Save Adjustment");
        saveAdjustmentButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelAdjustmentButton = new Button("Cancel");
        adjustmentButtons.getChildren().addAll(saveAdjustmentButton, cancelAdjustmentButton);
        adjustmentButtons.setAlignment(Pos.CENTER_RIGHT);

        adjustmentForm.getChildren().addAll(adjustmentTitle, adjustmentGrid, adjustmentButtons);
        adjustmentTab.setContent(adjustmentForm);

        tabPane.getTabs().addAll(inventoryTab, adjustmentTab);

        Scene newScene = new Scene(tabPane, 500, 500);
        newWindow.setTitle("Storage Management");
        newWindow.setScene(newScene);
        newWindow.show();

        // Update current quantity when book is selected
        bookAdjustCombo.setOnAction(e -> {
            String selectedBook = bookAdjustCombo.getValue();
            // Simulating getting the current quantity from a database
            if (selectedBook != null) {
                switch (selectedBook) {
                    case "Harry Potter and the Philosopher's Stone":
                        currentQuantityLabel.setText("58");
                        break;
                    case "The Shining":
                        currentQuantityLabel.setText("32");
                        break;
                    case "Mắt Biếc":
                        currentQuantityLabel.setText("15");
                        break;
                    case "The Da Vinci Code":
                        currentQuantityLabel.setText("40");
                        break;
                    case "Digital Fortress":
                        currentQuantityLabel.setText("0");
                        break;
                    default:
                        currentQuantityLabel.setText("0");
                }
            }
        });

        // Action handlers for cancel buttons
        cancelInventoryButton.setOnAction(e -> newWindow.close());
        cancelAdjustmentButton.setOnAction(e -> newWindow.close());

        // Action handlers for save buttons
        saveInventoryButton.setOnAction(e -> {
            // Save inventory logic here
            System.out.println(
                    "Saving new inventory for: " + bookCombo.getValue() + ", Quantity: " + quantityField.getText());
            newWindow.close();
        });

        saveAdjustmentButton.setOnAction(e -> {
            // Save adjustment logic here
            System.out.println("Saving adjustment for: " + bookAdjustCombo.getValue() +
                    ", Type: " + adjustmentTypeCombo.getValue() +
                    ", Quantity: " + adjustmentQuantityField.getText());
            newWindow.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Model classes
    public static class Inventory {
        private javafx.beans.property.SimpleStringProperty inventoryId;
        private javafx.beans.property.SimpleStringProperty bookId;
        private javafx.beans.property.SimpleStringProperty title;
        private javafx.beans.property.SimpleIntegerProperty quantity;
        private javafx.beans.property.SimpleStringProperty status;

        public Inventory(String inventoryId, String bookId, String title, int quantity, String status) {
            this.inventoryId = new javafx.beans.property.SimpleStringProperty(inventoryId);
            this.bookId = new javafx.beans.property.SimpleStringProperty(bookId);
            this.title = new javafx.beans.property.SimpleStringProperty(title);
            this.quantity = new javafx.beans.property.SimpleIntegerProperty(quantity);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public String getInventoryId() {
            return inventoryId.get();
        }

        public javafx.beans.property.SimpleStringProperty inventoryIdProperty() {
            return inventoryId;
        }

        public String getBookId() {
            return bookId.get();
        }

        public javafx.beans.property.SimpleStringProperty bookIdProperty() {
            return bookId;
        }

        public String getTitle() {
            return title.get();
        }

        public javafx.beans.property.SimpleStringProperty titleProperty() {
            return title;
        }

        public int getQuantity() {
            return quantity.get();
        }

        public javafx.beans.property.SimpleIntegerProperty quantityProperty() {
            return quantity;
        }

        public String getStatus() {
            return status.get();
        }

        public javafx.beans.property.SimpleStringProperty statusProperty() {
            return status;
        }
    }

    public static class Order {
        private javafx.beans.property.SimpleStringProperty orderId;
        private javafx.beans.property.SimpleStringProperty orderDate;
        private javafx.beans.property.SimpleDoubleProperty totalAmount;
        private javafx.beans.property.SimpleStringProperty status;
        private javafx.beans.property.SimpleStringProperty customerId;

        public Order(String orderId, String orderDate, double totalAmount, String status, String customerId) {
            this.orderId = new javafx.beans.property.SimpleStringProperty(orderId);
            this.orderDate = new javafx.beans.property.SimpleStringProperty(orderDate);
            this.totalAmount = new javafx.beans.property.SimpleDoubleProperty(totalAmount);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
            this.customerId = new javafx.beans.property.SimpleStringProperty(customerId);
        }

        public String getOrderId() {
            return orderId.get();
        }

        public javafx.beans.property.SimpleStringProperty orderIdProperty() {
            return orderId;
        }

        public String getOrderDate() {
            return orderDate.get();
        }

        public javafx.beans.property.SimpleStringProperty orderDateProperty() {
            return orderDate;
        }

        public double getTotalAmount() {
            return totalAmount.get();
        }

        public javafx.beans.property.SimpleDoubleProperty totalAmountProperty() {
            return totalAmount;
        }

        public String getStatus() {
            return status.get();
        }

        public javafx.beans.property.SimpleStringProperty statusProperty() {
            return status;
        }

        public String getCustomerId() {
            return customerId.get();
        }

        public javafx.beans.property.SimpleStringProperty customerIdProperty() {
            return customerId;
        }
    }
}