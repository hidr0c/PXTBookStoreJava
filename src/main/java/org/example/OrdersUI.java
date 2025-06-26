package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrdersUI extends Application {

    // Sample data for demonstration
    private ObservableList<Order> orderData = FXCollections.observableArrayList(
            new Order("ORD001", "2025-06-15", 159.99, "Completed", "CUST001", "Credit Card"),
            new Order("ORD002", "2025-06-18", 45.50, "Processing", "CUST003", "PayPal"),
            new Order("ORD003", "2025-06-20", 210.75, "Completed", "CUST002", "Cash"),
            new Order("ORD004", "2025-06-23", 78.25, "Shipped", "CUST004", "Credit Card"),
            new Order("ORD005", "2025-06-24", 124.99, "Processing", "CUST001", "Bank Transfer"));

    private ObservableList<Customer> customerData = FXCollections.observableArrayList(
            new Customer("CUST001", "Gold", "john.doe@example.com", 1254.75),
            new Customer("CUST002", "Silver", "jane.smith@example.com", 875.50),
            new Customer("CUST003", "Bronze", "robert.johnson@example.com", 325.25),
            new Customer("CUST004", "Gold", "maria.garcia@example.com", 1450.00),
            new Customer("CUST005", "Silver", "david.brown@example.com", 750.80));

    private ObservableList<OrderDetail> orderDetailData = FXCollections.observableArrayList(
            new OrderDetail("ORD001", "BOOK001", 15.99, 2),
            new OrderDetail("ORD001", "BOOK003", 8.50, 3),
            new OrderDetail("ORD001", "BOOK005", 11.99, 5),
            new OrderDetail("ORD002", "BOOK002", 12.99, 3),
            new OrderDetail("ORD002", "BOOK004", 14.99, 2),
            new OrderDetail("ORD003", "BOOK001", 15.99, 4),
            new OrderDetail("ORD003", "BOOK003", 8.50, 6),
            new OrderDetail("ORD003", "BOOK002", 12.99, 8),
            new OrderDetail("ORD004", "BOOK005", 11.99, 2),
            new OrderDetail("ORD004", "BOOK004", 14.99, 3),
            new OrderDetail("ORD005", "BOOK001", 15.99, 3),
            new OrderDetail("ORD005", "BOOK002", 12.99, 6));

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Order Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Search and buttons section
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Search orders by ID or customer...");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;");

        Button createNewButton = new Button("Create New Order");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewOrderWindow());

        actionBar.getChildren().addAll(searchField, searchButton, createNewButton);

        // Order statistics section
        HBox statsBar = new HBox(20);
        statsBar.setPadding(new Insets(10));
        statsBar.setAlignment(Pos.CENTER);

        VBox totalOrdersStats = createStatBox("Total Orders", "124");
        VBox pendingOrdersStats = createStatBox("Pending Orders", "37");
        VBox completedOrdersStats = createStatBox("Completed", "82");
        VBox todayOrdersStats = createStatBox("Today's Orders", "5");

        statsBar.getChildren().addAll(totalOrdersStats, pendingOrdersStats, completedOrdersStats, todayOrdersStats);

        // Date range selection
        HBox dateRangeBar = new HBox(10);
        dateRangeBar.setAlignment(Pos.CENTER);
        dateRangeBar.setPadding(new Insets(5));

        Label fromLabel = new Label("From:");
        DatePicker fromDatePicker = new DatePicker(LocalDate.now().minusDays(30));

        Label toLabel = new Label("To:");
        DatePicker toDatePicker = new DatePicker(LocalDate.now());

        Button applyFilterButton = new Button("Apply Filter");
        applyFilterButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        dateRangeBar.getChildren().addAll(fromLabel, fromDatePicker, toLabel, toDatePicker, applyFilterButton);

        topSection.getChildren().addAll(titleLabel, actionBar, statsBar, dateRangeBar);
        root.setTop(topSection);

        // Main content with TabPane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Orders tab
        Tab ordersTab = new Tab("Orders");
        VBox ordersBox = new VBox(10);
        ordersBox.setPadding(new Insets(10));

        // Split pane for order list and details
        SplitPane orderSplitPane = new SplitPane();

        // Orders table
        TableView<Order> ordersTable = new TableView<>();
        ordersTable.setPrefHeight(250);
        ordersTable.setItems(orderData);

        TableColumn<Order, String> orderIdColumn = new TableColumn<>("Order ID");
        orderIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        orderIdColumn.setPrefWidth(80);

        TableColumn<Order, String> orderDateColumn = new TableColumn<>("Order Date");
        orderDateColumn.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        orderDateColumn.setPrefWidth(100);

        TableColumn<Order, Number> totalAmountColumn = new TableColumn<>("Total Amount");
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
                        setText(String.format("$%.2f", item.doubleValue()));
                    }
                }
            };
        });

        TableColumn<Order, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusColumn.setPrefWidth(100);
        statusColumn.setCellFactory(column -> {
            return new TableCell<Order, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item) {
                            case "Completed":
                                setStyle("-fx-text-fill: green;");
                                break;
                            case "Processing":
                                setStyle("-fx-text-fill: blue;");
                                break;
                            case "Shipped":
                                setStyle("-fx-text-fill: orange;");
                                break;
                            default:
                                setStyle("");
                                break;
                        }
                    }
                }
            };
        });

        TableColumn<Order, String> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        customerIdColumn.setPrefWidth(100);

        TableColumn<Order, String> methodTypeColumn = new TableColumn<>("Payment Method");
        methodTypeColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());
        methodTypeColumn.setPrefWidth(120);

        TableColumn<Order, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setPrefWidth(200);
        actionColumn.setCellFactory(param -> new TableCell<Order, Void>() {
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            private final Button printBtn = new Button("Print");
            private final Button cancelBtn = new Button("Cancel");

            {
                viewBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                printBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
                cancelBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                viewBtn.setOnAction(event -> {
                    Order data = getTableView().getItems().get(getIndex());
                    System.out.println("View order details for: " + data.getOrderId());
                    showOrderDetails(data);
                });

                editBtn.setOnAction(event -> {
                    Order data = getTableView().getItems().get(getIndex());
                    System.out.println("Edit order: " + data.getOrderId());
                });

                printBtn.setOnAction(event -> {
                    Order data = getTableView().getItems().get(getIndex());
                    System.out.println("Print invoice for: " + data.getOrderId());
                });

                cancelBtn.setOnAction(event -> {
                    Order data = getTableView().getItems().get(getIndex());
                    System.out.println("Cancel order: " + data.getOrderId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox pane = new HBox(5);
                    pane.setAlignment(Pos.CENTER_LEFT);
                    Order order = getTableView().getItems().get(getIndex());

                    // Only show relevant buttons based on order status
                    pane.getChildren().add(viewBtn);

                    if (!"Completed".equals(order.getStatus())) {
                        pane.getChildren().add(editBtn);
                    }

                    if ("Completed".equals(order.getStatus())) {
                        pane.getChildren().add(printBtn);
                    }

                    if (!"Completed".equals(order.getStatus()) && !"Shipped".equals(order.getStatus())) {
                        pane.getChildren().add(cancelBtn);
                    }

                    setGraphic(pane);
                }
            }
        });

        ordersTable.getColumns().addAll(orderIdColumn, orderDateColumn, totalAmountColumn, statusColumn,
                customerIdColumn, methodTypeColumn, actionColumn);

        // Order details section - will be populated when an order is selected
        VBox orderDetailsPane = new VBox(10);
        orderDetailsPane.setPadding(new Insets(10));
        orderDetailsPane.setStyle("-fx-background-color: #f8f9fa;");

        Label orderDetailsTitle = new Label("Order Details");
        orderDetailsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Table for order items
        TableView<OrderDetail> orderItemsTable = new TableView<>();
        orderItemsTable.setPrefHeight(200);

        TableColumn<OrderDetail, String> orderItemIdColumn = new TableColumn<>("Order ID");
        orderItemIdColumn.setCellValueFactory(cellData -> cellData.getValue().orderIdProperty());
        orderItemIdColumn.setPrefWidth(80);

        TableColumn<OrderDetail, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        bookIdColumn.setPrefWidth(80);

        TableColumn<OrderDetail, Number> unitPriceColumn = new TableColumn<>("Unit Price");
        unitPriceColumn.setCellValueFactory(cellData -> cellData.getValue().unitPriceProperty());
        unitPriceColumn.setPrefWidth(100);
        unitPriceColumn.setCellFactory(column -> {
            return new TableCell<OrderDetail, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", item.doubleValue()));
                    }
                }
            };
        });

        TableColumn<OrderDetail, Number> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        quantityColumn.setPrefWidth(80);

        TableColumn<OrderDetail, Number> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(
                cellData -> cellData.getValue().unitPriceProperty().multiply(cellData.getValue().quantityProperty()));
        subtotalColumn.setPrefWidth(100);
        subtotalColumn.setCellFactory(column -> {
            return new TableCell<OrderDetail, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", item.doubleValue()));
                    }
                }
            };
        });

        orderItemsTable.getColumns().addAll(orderItemIdColumn, bookIdColumn, unitPriceColumn, quantityColumn,
                subtotalColumn);

        // Customer information area
        TitledPane customerInfoPane = new TitledPane("Customer Information",
                new Label("Select an order to view customer details"));
        customerInfoPane.setCollapsible(false);

        // Order summary area
        GridPane orderSummary = new GridPane();
        orderSummary.setHgap(10);
        orderSummary.setVgap(5);
        orderSummary.setPadding(new Insets(10));

        orderSummary.add(new Label("Subtotal:"), 0, 0);
        Label subtotalLabel = new Label("$0.00");
        orderSummary.add(subtotalLabel, 1, 0);

        orderSummary.add(new Label("Tax:"), 0, 1);
        Label taxLabel = new Label("$0.00");
        orderSummary.add(taxLabel, 1, 1);

        orderSummary.add(new Label("Shipping:"), 0, 2);
        Label shippingLabel = new Label("$0.00");
        orderSummary.add(shippingLabel, 1, 2);

        orderSummary.add(new Label("Total:"), 0, 3);
        Label totalLabel = new Label("$0.00");
        totalLabel.setStyle("-fx-font-weight: bold;");
        orderSummary.add(totalLabel, 1, 3);

        TitledPane orderSummaryPane = new TitledPane("Order Summary", orderSummary);
        orderSummaryPane.setCollapsible(false);

        orderDetailsPane.getChildren().addAll(orderDetailsTitle, orderItemsTable, customerInfoPane, orderSummaryPane);

        orderSplitPane.getItems().addAll(ordersTable, orderDetailsPane);
        orderSplitPane.setDividerPositions(0.6);

        ordersBox.getChildren().addAll(orderSplitPane);
        ordersTab.setContent(ordersBox);

        // Customers tab
        Tab customersTab = new Tab("Customers");
        VBox customersBox = new VBox(10);
        customersBox.setPadding(new Insets(10));

        // Customers table
        TableView<Customer> customersTable = new TableView<>();
        customersTable.setPrefHeight(300);
        customersTable.setItems(customerData);

        TableColumn<Customer, String> customerIdCustomerColumn = new TableColumn<>("Customer ID");
        customerIdCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        customerIdCustomerColumn.setPrefWidth(100);

        TableColumn<Customer, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty());
        rankColumn.setPrefWidth(100);
        rankColumn.setCellFactory(column -> {
            return new TableCell<Customer, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        switch (item) {
                            case "Gold":
                                setStyle("-fx-text-fill: #FFD700;");
                                break;
                            case "Silver":
                                setStyle("-fx-text-fill: #C0C0C0;");
                                break;
                            case "Bronze":
                                setStyle("-fx-text-fill: #CD7F32;");
                                break;
                            default:
                                setStyle("");
                                break;
                        }
                    }
                }
            };
        });

        TableColumn<Customer, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setPrefWidth(200);

        TableColumn<Customer, Number> spendingColumn = new TableColumn<>("Total Spending");
        spendingColumn.setCellValueFactory(cellData -> cellData.getValue().spendingProperty());
        spendingColumn.setPrefWidth(120);
        spendingColumn.setCellFactory(column -> {
            return new TableCell<Customer, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", item.doubleValue()));
                    }
                }
            };
        });

        TableColumn<Customer, Void> customerActionColumn = new TableColumn<>("Actions");
        customerActionColumn.setPrefWidth(200);
        customerActionColumn.setCellFactory(param -> new TableCell<Customer, Void>() {
            private final Button viewBtn = new Button("View Orders");
            private final Button editBtn = new Button("Edit");
            private final Button contactBtn = new Button("Contact");

            {
                viewBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                contactBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");

                viewBtn.setOnAction(event -> {
                    Customer data = getTableView().getItems().get(getIndex());
                    System.out.println("View orders for customer: " + data.getCustomerId());
                });

                editBtn.setOnAction(event -> {
                    Customer data = getTableView().getItems().get(getIndex());
                    System.out.println("Edit customer: " + data.getCustomerId());
                });

                contactBtn.setOnAction(event -> {
                    Customer data = getTableView().getItems().get(getIndex());
                    System.out.println("Contact customer: " + data.getCustomerId());
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
                    pane.getChildren().addAll(viewBtn, editBtn, contactBtn);
                    setGraphic(pane);
                }
            }
        });

        customersTable.getColumns().addAll(customerIdCustomerColumn, rankColumn, emailColumn, spendingColumn,
                customerActionColumn);

        // Customer statistics section
        HBox customerStatsSection = new HBox(20);
        customerStatsSection.setPadding(new Insets(10));

        // Pie chart for customer distribution
        PieChart customerChart = new PieChart();
        customerChart.setTitle("Customer Distribution by Rank");
        customerChart.getData().add(new PieChart.Data("Gold", 35));
        customerChart.getData().add(new PieChart.Data("Silver", 45));
        customerChart.getData().add(new PieChart.Data("Bronze", 20));

        // Top customers list
        VBox topCustomersBox = new VBox(10);
        Label topCustomersLabel = new Label("Top Customers");
        topCustomersLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        ListView<String> topCustomersList = new ListView<>();
        topCustomersList.getItems().addAll(
                "Maria Garcia - $1,450.00",
                "John Doe - $1,254.75",
                "Jane Smith - $875.50",
                "David Brown - $750.80",
                "Robert Johnson - $325.25");

        topCustomersBox.getChildren().addAll(topCustomersLabel, topCustomersList);

        customerStatsSection.getChildren().addAll(customerChart, topCustomersBox);
        HBox.setHgrow(customerChart, Priority.ALWAYS);
        HBox.setHgrow(topCustomersBox, Priority.ALWAYS);

        customersBox.getChildren().addAll(customersTable, customerStatsSection);
        customersTab.setContent(customersBox);

        // Analytics tab
        Tab analyticsTab = new Tab("Analytics");
        VBox analyticsBox = new VBox(10);
        analyticsBox.setPadding(new Insets(10));

        Label analyticsLabel = new Label("Order Analytics");
        analyticsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Filter controls for analytics
        HBox analyticsFilters = new HBox(10);
        analyticsFilters.setPadding(new Insets(10));
        analyticsFilters.setAlignment(Pos.CENTER_LEFT);

        Label periodLabel = new Label("Period:");
        ComboBox<String> periodComboBox = new ComboBox<>();
        periodComboBox.getItems().addAll("Last 7 Days", "Last 30 Days", "Last 90 Days", "Last Year", "Custom");
        periodComboBox.setValue("Last 30 Days");

        Button refreshButton = new Button("Refresh");
        refreshButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        analyticsFilters.getChildren().addAll(periodLabel, periodComboBox, refreshButton);

        // Analytics cards
        HBox analyticsCards = new HBox(20);
        analyticsCards.setPadding(new Insets(10));

        VBox totalSalesBox = createStatBox("Total Sales", "$4,352.75");
        VBox avgOrderValueBox = createStatBox("Avg Order Value", "$87.05");
        VBox repeatCustomersBox = createStatBox("Repeat Customers", "65%");
        VBox conversionRateBox = createStatBox("Conversion Rate", "23%");

        analyticsCards.getChildren().addAll(totalSalesBox, avgOrderValueBox, repeatCustomersBox, conversionRateBox);

        // Placeholder for charts
        Label chartsLabel = new Label("Sales Trend Charts (Placeholder)");
        chartsLabel.setPadding(new Insets(50));
        chartsLabel.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px; -fx-background-color: #f8f9fa;");
        chartsLabel.setMaxWidth(Double.MAX_VALUE);
        chartsLabel.setAlignment(Pos.CENTER);

        analyticsBox.getChildren().addAll(analyticsLabel, analyticsFilters, analyticsCards, chartsLabel);
        analyticsTab.setContent(analyticsBox);

        tabPane.getTabs().addAll(ordersTab, customersTab, analyticsTab);
        root.setCenter(tabPane);

        // Selection handler for orders table
        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Filter order details for selected order
                ObservableList<OrderDetail> filteredDetails = FXCollections.observableArrayList();
                for (OrderDetail detail : orderDetailData) {
                    if (detail.getOrderId().equals(newSelection.getOrderId())) {
                        filteredDetails.add(detail);
                    }
                }
                orderItemsTable.setItems(filteredDetails);

                // Update customer info
                for (Customer customer : customerData) {
                    if (customer.getCustomerId().equals(newSelection.getCustomerId())) {
                        VBox customerDetails = new VBox(5);
                        customerDetails.getChildren().addAll(
                                new Label("Customer ID: " + customer.getCustomerId()),
                                new Label("Rank: " + customer.getRank()),
                                new Label("Email: " + customer.getEmail()),
                                new Label("Total Spending: $" + String.format("%.2f", customer.getSpending())));
                        customerInfoPane.setContent(customerDetails);
                        break;
                    }
                }

                // Update order summary
                double subtotal = 0.0;
                for (OrderDetail detail : filteredDetails) {
                    subtotal += detail.getUnitPrice() * detail.getQuantity();
                }
                double tax = subtotal * 0.1; // 10% tax
                double shipping = 5.99;
                double total = subtotal + tax + shipping;

                subtotalLabel.setText(String.format("$%.2f", subtotal));
                taxLabel.setText(String.format("$%.2f", tax));
                shippingLabel.setText(String.format("$%.2f", shipping));
                totalLabel.setText(String.format("$%.2f", total));
            }
        });

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Book Store Management - Orders");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showOrderDetails(Order order) {
        // Implementation would display a detailed view of the order
        System.out.println("Showing detailed view for order: " + order.getOrderId());
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

    private void showCreateNewOrderWindow() {
        Stage newWindow = new Stage();

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label titleLabel = new Label("Create New Order");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Customer selection
        HBox customerSection = new HBox(10);
        customerSection.setAlignment(Pos.CENTER_LEFT);

        Label customerLabel = new Label("Customer:");
        ComboBox<String> customerComboBox = new ComboBox<>();
        customerComboBox.getItems().addAll(
                "CUST001 - John Doe",
                "CUST002 - Jane Smith",
                "CUST003 - Robert Johnson",
                "CUST004 - Maria Garcia",
                "CUST005 - David Brown");
        customerComboBox.setPrefWidth(250);

        Button newCustomerButton = new Button("New Customer");
        newCustomerButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        customerSection.getChildren().addAll(customerLabel, customerComboBox, newCustomerButton);

        // Order items section
        Label itemsLabel = new Label("Order Items:");
        itemsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TableView<OrderDetail> orderItemsTable = new TableView<>();
        orderItemsTable.setPrefHeight(200);

        TableColumn<OrderDetail, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setPrefWidth(100);

        TableColumn<OrderDetail, String> bookTitleColumn = new TableColumn<>("Book Title");
        bookTitleColumn.setPrefWidth(200);

        TableColumn<OrderDetail, Number> priceColumn = new TableColumn<>("Price");
        priceColumn.setPrefWidth(80);

        TableColumn<OrderDetail, Number> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setPrefWidth(80);

        TableColumn<OrderDetail, Number> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setPrefWidth(80);

        orderItemsTable.getColumns().addAll(bookIdColumn, bookTitleColumn, priceColumn, quantityColumn, subtotalColumn);

        // Add items controls
        HBox addItemControls = new HBox(10);
        addItemControls.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> productComboBox = new ComboBox<>();
        productComboBox.getItems().addAll(
                "BOOK001 - Harry Potter and the Philosopher's Stone",
                "BOOK002 - The Shining",
                "BOOK003 - Mắt Biếc",
                "BOOK004 - The Da Vinci Code",
                "BOOK005 - Digital Fortress");
        productComboBox.setPrefWidth(300);

        Spinner<Integer> quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(80);

        Button addItemButton = new Button("Add Item");
        addItemButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        addItemControls.getChildren().addAll(productComboBox, quantitySpinner, addItemButton);

        // Order summary
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(10);
        summaryGrid.setVgap(5);
        summaryGrid.setPadding(new Insets(10));

        summaryGrid.add(new Label("Subtotal:"), 0, 0);
        summaryGrid.add(new Label("$0.00"), 1, 0);

        summaryGrid.add(new Label("Tax:"), 0, 1);
        summaryGrid.add(new Label("$0.00"), 1, 1);

        summaryGrid.add(new Label("Shipping:"), 0, 2);
        summaryGrid.add(new Label("$5.99"), 1, 2);

        summaryGrid.add(new Label("Total:"), 0, 3);
        Label totalLabel = new Label("$5.99");
        totalLabel.setStyle("-fx-font-weight: bold;");
        summaryGrid.add(totalLabel, 1, 3);

        // Payment method
        HBox paymentSection = new HBox(10);
        paymentSection.setAlignment(Pos.CENTER_LEFT);

        Label paymentMethodLabel = new Label("Payment Method:");
        ComboBox<String> paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Credit Card", "PayPal", "Bank Transfer", "Cash");

        paymentSection.getChildren().addAll(paymentMethodLabel, paymentMethodCombo);

        // Buttons
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button saveButton = new Button("Create Order");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");

        Button cancelButton = new Button("Cancel");

        buttonBar.getChildren().addAll(cancelButton, saveButton);

        root.getChildren().addAll(
                titleLabel,
                customerSection,
                new Separator(),
                itemsLabel,
                orderItemsTable,
                addItemControls,
                new Separator(),
                summaryGrid,
                paymentSection,
                buttonBar);

        Scene scene = new Scene(root, 600, 600);
        newWindow.setTitle("Create New Order");
        newWindow.setScene(scene);
        newWindow.show();

        // Action handlers
        cancelButton.setOnAction(e -> newWindow.close());
        saveButton.setOnAction(e -> {
            // Save order logic here
            newWindow.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Model classes
    public static class Order {
        private javafx.beans.property.SimpleStringProperty orderId;
        private javafx.beans.property.SimpleStringProperty orderDate;
        private javafx.beans.property.SimpleDoubleProperty totalAmount;
        private javafx.beans.property.SimpleStringProperty status;
        private javafx.beans.property.SimpleStringProperty customerId;
        private javafx.beans.property.SimpleStringProperty paymentMethod;

        public Order(String id, String date, double amount, String status, String customerId, String paymentMethod) {
            this.orderId = new javafx.beans.property.SimpleStringProperty(id);
            this.orderDate = new javafx.beans.property.SimpleStringProperty(date);
            this.totalAmount = new javafx.beans.property.SimpleDoubleProperty(amount);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
            this.customerId = new javafx.beans.property.SimpleStringProperty(customerId);
            this.paymentMethod = new javafx.beans.property.SimpleStringProperty(paymentMethod);
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

        public String getPaymentMethod() {
            return paymentMethod.get();
        }

        public javafx.beans.property.SimpleStringProperty paymentMethodProperty() {
            return paymentMethod;
        }
    }

    public static class Customer {
        private javafx.beans.property.SimpleStringProperty customerId;
        private javafx.beans.property.SimpleStringProperty rank;
        private javafx.beans.property.SimpleStringProperty email;
        private javafx.beans.property.SimpleDoubleProperty spending;

        public Customer(String id, String rank, String email, double spending) {
            this.customerId = new javafx.beans.property.SimpleStringProperty(id);
            this.rank = new javafx.beans.property.SimpleStringProperty(rank);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.spending = new javafx.beans.property.SimpleDoubleProperty(spending);
        }

        public String getCustomerId() {
            return customerId.get();
        }

        public javafx.beans.property.SimpleStringProperty customerIdProperty() {
            return customerId;
        }

        public String getRank() {
            return rank.get();
        }

        public javafx.beans.property.SimpleStringProperty rankProperty() {
            return rank;
        }

        public String getEmail() {
            return email.get();
        }

        public javafx.beans.property.SimpleStringProperty emailProperty() {
            return email;
        }

        public double getSpending() {
            return spending.get();
        }

        public javafx.beans.property.SimpleDoubleProperty spendingProperty() {
            return spending;
        }
    }

    public static class OrderDetail {
        private javafx.beans.property.SimpleStringProperty orderId;
        private javafx.beans.property.SimpleStringProperty bookId;
        private javafx.beans.property.SimpleDoubleProperty unitPrice;
        private javafx.beans.property.SimpleIntegerProperty quantity;

        public OrderDetail(String orderId, String bookId, double unitPrice, int quantity) {
            this.orderId = new javafx.beans.property.SimpleStringProperty(orderId);
            this.bookId = new javafx.beans.property.SimpleStringProperty(bookId);
            this.unitPrice = new javafx.beans.property.SimpleDoubleProperty(unitPrice);
            this.quantity = new javafx.beans.property.SimpleIntegerProperty(quantity);
        }

        public String getOrderId() {
            return orderId.get();
        }

        public javafx.beans.property.SimpleStringProperty orderIdProperty() {
            return orderId;
        }

        public String getBookId() {
            return bookId.get();
        }

        public javafx.beans.property.SimpleStringProperty bookIdProperty() {
            return bookId;
        }

        public double getUnitPrice() {
            return unitPrice.get();
        }

        public javafx.beans.property.SimpleDoubleProperty unitPriceProperty() {
            return unitPrice;
        }

        public int getQuantity() {
            return quantity.get();
        }

        public javafx.beans.property.SimpleIntegerProperty quantityProperty() {
            return quantity;
        }
    }
}