package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class DashboardUI extends Application {
    private Stage primaryStage;
    private BorderPane mainLayout;
    private StackPane contentArea;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showDashboard();
    }

    public void showDashboard() {
        mainLayout = new BorderPane();

        VBox drawer = new VBox(10);
        drawer.setPadding(new Insets(10));
        drawer.setStyle("-fx-background-color: #f0f0f0;");
        drawer.setPrefWidth(200);

        Button dashboardButton = createNavButton("Dashboard");
        Button storageButton = createNavButton("Quản lý kho");
        Button productButton = createNavButton("Quản lý sản phẩm");
        Button staffButton = createNavButton("Quản lý nhân viên");
        Button ordersButton = createNavButton("Quản lý hóa đơn");

        Button[] buttons = { dashboardButton, storageButton, productButton, staffButton, ordersButton };
        for (Button btn : buttons) {
            btn.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(btn, Priority.ALWAYS);
            btn.setPadding(new Insets(10));
        }

        drawer.getChildren().addAll(buttons);
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        showDashboardContent();

        mainLayout.setLeft(drawer);
        mainLayout.setCenter(contentArea);

        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setTitle("Book Store Management System");
        primaryStage.setScene(scene);
        primaryStage.show(); // Add action listeners for navigation buttons
        dashboardButton.setOnAction(e -> showDashboardContent());
        storageButton.setOnAction(e -> showStorageUI());
        productButton.setOnAction(e -> showProductsUI());
        staffButton.setOnAction(e -> showStaffUI());
        ordersButton.setOnAction(e -> showOrdersUI());
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(60);
        button.setStyle(
                "-fx-background-color: #0066cc; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        return button;
    }

    private void showDashboardContent() {
        contentArea.getChildren().clear();

        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Welcome to the Book Store Management System");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Summary tables

        // Recent Orders table
        TableView<Object> recentOrdersTable = new TableView<>();
        recentOrdersTable.setPrefHeight(200);

        TableColumn<Object, String> orderIdColumn = new TableColumn<>("Order ID");
        TableColumn<Object, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Object, String> customerColumn = new TableColumn<>("Customer");
        TableColumn<Object, String> totalColumn = new TableColumn<>("Total");

        recentOrdersTable.getColumns().add(orderIdColumn);
        recentOrdersTable.getColumns().add(dateColumn);
        recentOrdersTable.getColumns().add(customerColumn);
        recentOrdersTable.getColumns().add(totalColumn);

        Label recentOrdersLabel = new Label("Recent Orders");
        recentOrdersLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Low Stock Items table
        TableView<Object> lowStockTable = new TableView<>();
        lowStockTable.setPrefHeight(200);

        TableColumn<Object, String> bookIdColumn = new TableColumn<>("Book ID");
        TableColumn<Object, String> titleColumn = new TableColumn<>("Title");
        TableColumn<Object, String> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Object, String> reorderColumn = new TableColumn<>("Reorder Level");

        lowStockTable.getColumns().add(bookIdColumn);
        lowStockTable.getColumns().add(titleColumn);
        lowStockTable.getColumns().add(quantityColumn);
        lowStockTable.getColumns().add(reorderColumn);

        Label lowStockLabel = new Label("Low Stock Items");
        lowStockLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        dashboardContent.getChildren().addAll(welcomeLabel, recentOrdersLabel, recentOrdersTable, lowStockLabel,
                lowStockTable);
        contentArea.getChildren().add(dashboardContent);
    }

    private void showStorageUI() {
        contentArea.getChildren().clear();

        try {
            BorderPane storageContent = new BorderPane();

            // Add the StorageUI content to the main content area
            VBox topContent = new VBox();
            Label titleLabel = new Label("Storage Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Button createNewButton = new Button("Create New Storage Item");
            createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

            topContent.getChildren().addAll(titleLabel, createNewButton);
            topContent.setSpacing(10);
            topContent.setPadding(new Insets(10));

            // Storage table (related to OrderDetails)
            TableView<Object> storageTable = new TableView<>();
            storageTable.setPrefHeight(200);

            TableColumn<Object, String> orderIdColumn = new TableColumn<>("Order ID");
            TableColumn<Object, String> bookIdColumn = new TableColumn<>("Book ID");
            TableColumn<Object, String> unitPriceColumn = new TableColumn<>("Unit Price");
            TableColumn<Object, String> quantityColumn = new TableColumn<>("Quantity");
            TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

            storageTable.getColumns().add(orderIdColumn);
            storageTable.getColumns().add(bookIdColumn);
            storageTable.getColumns().add(unitPriceColumn);
            storageTable.getColumns().add(quantityColumn);
            storageTable.getColumns().add(methodTypeColumn);

            // Orders table (related to Storage)
            TableView<Object> ordersTable = new TableView<>();
            ordersTable.setPrefHeight(200);

            TableColumn<Object, String> orderIdOrderColumn = new TableColumn<>("Order ID");
            TableColumn<Object, String> orderDateColumn = new TableColumn<>("Order Date");
            TableColumn<Object, String> totalAmountColumn = new TableColumn<>("Total Amount");
            TableColumn<Object, String> statusColumn = new TableColumn<>("Status");
            TableColumn<Object, String> customerIdColumn = new TableColumn<>("Customer ID");
            TableColumn<Object, String> methodTypeOrderColumn = new TableColumn<>("Method Type");

            ordersTable.getColumns().add(orderIdOrderColumn);
            ordersTable.getColumns().add(orderDateColumn);
            ordersTable.getColumns().add(totalAmountColumn);
            ordersTable.getColumns().add(statusColumn);
            ordersTable.getColumns().add(customerIdColumn);
            ordersTable.getColumns().add(methodTypeOrderColumn);

            // Add tables to layout
            VBox tablesSection = new VBox(20);
            tablesSection.setPadding(new Insets(10));

            Label storageLabel = new Label("Storage Details:");
            storageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label orderLabel = new Label("Order Details:");
            orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            tablesSection.getChildren().addAll(
                    storageLabel, storageTable,
                    orderLabel, ordersTable);

            storageContent.setTop(topContent);
            storageContent.setCenter(tablesSection);
            contentArea.getChildren().add(storageContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showProductsUI() {
        contentArea.getChildren().clear();

        try {
            // Create the ProductsUI content directly in the contentArea
            BorderPane productsContent = new BorderPane();

            VBox topContent = new VBox();
            Label titleLabel = new Label("Product Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Button createNewButton = new Button("Create New Product");
            createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

            topContent.getChildren().addAll(titleLabel, createNewButton);
            topContent.setSpacing(10);
            topContent.setPadding(new Insets(10));

            // Publishers table
            TableView<Object> publishersTable = new TableView<>();
            publishersTable.setPrefHeight(150);

            TableColumn<Object, String> publisherIdColumn = new TableColumn<>("Publisher ID");
            TableColumn<Object, String> publisherNameColumn = new TableColumn<>("Publisher Name");
            TableColumn<Object, String> publishDateColumn = new TableColumn<>("Publish Date");
            TableColumn<Object, String> languageColumn = new TableColumn<>("Language");

            publishersTable.getColumns().add(publisherIdColumn);
            publishersTable.getColumns().add(publisherNameColumn);
            publishersTable.getColumns().add(publishDateColumn);
            publishersTable.getColumns().add(languageColumn);

            // Authors table
            TableView<Object> authorsTable = new TableView<>();
            authorsTable.setPrefHeight(150);

            TableColumn<Object, String> authorIdColumn = new TableColumn<>("Author ID");
            TableColumn<Object, String> authorNameColumn = new TableColumn<>("Author Name");
            TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

            authorsTable.getColumns().add(authorIdColumn);
            authorsTable.getColumns().add(authorNameColumn);
            authorsTable.getColumns().add(methodTypeColumn);

            // Books table
            TableView<Object> booksTable = new TableView<>();
            booksTable.setPrefHeight(150);

            TableColumn<Object, String> bookIdColumn = new TableColumn<>("Book ID");
            TableColumn<Object, String> bookTitleColumn = new TableColumn<>("Title");
            TableColumn<Object, String> descriptionColumn = new TableColumn<>("Description");
            TableColumn<Object, String> pageColumn = new TableColumn<>("Pages");
            TableColumn<Object, String> priceColumn = new TableColumn<>("Price");

            booksTable.getColumns().add(bookIdColumn);
            booksTable.getColumns().add(bookTitleColumn);
            booksTable.getColumns().add(descriptionColumn);
            booksTable.getColumns().add(pageColumn);
            booksTable.getColumns().add(priceColumn);

            VBox tablesSection = new VBox(20);
            tablesSection.setPadding(new Insets(10));

            Label publisherLabel = new Label("Publisher Details:");
            publisherLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label authorLabel = new Label("Author Details:");
            authorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label bookLabel = new Label("Book Details:");
            bookLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            tablesSection.getChildren().addAll(
                    publisherLabel, publishersTable,
                    authorLabel, authorsTable,
                    bookLabel, booksTable);

            productsContent.setTop(topContent);
            productsContent.setCenter(tablesSection);
            contentArea.getChildren().add(productsContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showStaffUI() {
        contentArea.getChildren().clear();

        try {
            BorderPane staffContent = new BorderPane();

            VBox topContent = new VBox();
            Label titleLabel = new Label("Staff Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Button createNewButton = new Button("Create New Staff");
            createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

            topContent.getChildren().addAll(titleLabel, createNewButton);
            topContent.setSpacing(10);
            topContent.setPadding(new Insets(10));

            // Staff table
            TableView<Object> staffTable = new TableView<>();
            staffTable.setPrefHeight(300);

            TableColumn<Object, String> staffIdColumn = new TableColumn<>("Staff ID");
            TableColumn<Object, String> positionColumn = new TableColumn<>("Position");
            TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

            staffTable.getColumns().add(staffIdColumn);
            staffTable.getColumns().add(positionColumn);
            staffTable.getColumns().add(methodTypeColumn);

            TableView<Object> userTable = new TableView<>();
            userTable.setPrefHeight(300);

            TableColumn<Object, String> userIdColumn = new TableColumn<>("User ID");
            TableColumn<Object, String> fullNameColumn = new TableColumn<>("Full Name");
            TableColumn<Object, String> addressColumn = new TableColumn<>("Address");
            TableColumn<Object, String> phoneNumberColumn = new TableColumn<>("Phone Number");
            TableColumn<Object, String> methodTypeUserColumn = new TableColumn<>("Method Type");

            userTable.getColumns().add(userIdColumn);
            userTable.getColumns().add(fullNameColumn);
            userTable.getColumns().add(addressColumn);
            userTable.getColumns().add(phoneNumberColumn);
            userTable.getColumns().add(methodTypeUserColumn);

            VBox tablesSection = new VBox(20);
            tablesSection.setPadding(new Insets(10));

            Label staffLabel = new Label("Staff Details:");
            staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label userLabel = new Label("User Details:");
            userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            tablesSection.getChildren().addAll(
                    staffLabel, staffTable,
                    userLabel, userTable);

            staffContent.setTop(topContent);
            staffContent.setCenter(tablesSection);
            contentArea.getChildren().add(staffContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showOrdersUI() {
        contentArea.getChildren().clear();

        try {
            BorderPane ordersContent = new BorderPane();

            VBox topContent = new VBox();
            Label titleLabel = new Label("Order Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Button createNewButton = new Button("Create New Order");
            createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

            topContent.getChildren().addAll(titleLabel, createNewButton);
            topContent.setSpacing(10);
            topContent.setPadding(new Insets(10));

            TableView<Object> ordersTable = new TableView<>();
            ordersTable.setPrefHeight(200);

            TableColumn<Object, String> orderIdColumn = new TableColumn<>("Order ID");
            TableColumn<Object, String> orderDateColumn = new TableColumn<>("Order Date");
            TableColumn<Object, String> totalAmountColumn = new TableColumn<>("Total Amount");
            TableColumn<Object, String> statusColumn = new TableColumn<>("Status");
            TableColumn<Object, String> customerIdColumn = new TableColumn<>("Customer ID");
            TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

            ordersTable.getColumns().add(orderIdColumn);
            ordersTable.getColumns().add(orderDateColumn);
            ordersTable.getColumns().add(totalAmountColumn);
            ordersTable.getColumns().add(statusColumn);
            ordersTable.getColumns().add(customerIdColumn);
            ordersTable.getColumns().add(methodTypeColumn);

            TableView<Object> customersTable = new TableView<>();
            customersTable.setPrefHeight(150);

            TableColumn<Object, String> customerIdCustomerColumn = new TableColumn<>("Customer ID");
            TableColumn<Object, String> rankColumn = new TableColumn<>("Rank");
            TableColumn<Object, String> emailColumn = new TableColumn<>("Email");
            TableColumn<Object, String> spendingColumn = new TableColumn<>("Spending");
            TableColumn<Object, String> methodTypeCustomerColumn = new TableColumn<>("Method Type");

            customersTable.getColumns().add(customerIdCustomerColumn);
            customersTable.getColumns().add(rankColumn);
            customersTable.getColumns().add(emailColumn);
            customersTable.getColumns().add(spendingColumn);
            customersTable.getColumns().add(methodTypeCustomerColumn);

            TableView<Object> orderDetailsTable = new TableView<>();
            orderDetailsTable.setPrefHeight(150);

            TableColumn<Object, String> orderIdOrderDetailsColumn = new TableColumn<>("Order ID");
            TableColumn<Object, String> bookIdOrderDetailsColumn = new TableColumn<>("Book ID");
            TableColumn<Object, String> unitPriceOrderDetailsColumn = new TableColumn<>("Unit Price");
            TableColumn<Object, String> quantityOrderDetailsColumn = new TableColumn<>("Quantity");
            TableColumn<Object, String> methodTypeOrderDetailsColumn = new TableColumn<>("Method Type");

            orderDetailsTable.getColumns().add(orderIdOrderDetailsColumn);
            orderDetailsTable.getColumns().add(bookIdOrderDetailsColumn);
            orderDetailsTable.getColumns().add(unitPriceOrderDetailsColumn);
            orderDetailsTable.getColumns().add(quantityOrderDetailsColumn);
            orderDetailsTable.getColumns().add(methodTypeOrderDetailsColumn);

            VBox tablesSection = new VBox(20);
            tablesSection.setPadding(new Insets(10));

            Label orderLabel = new Label("Order Details:");
            orderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label customerLabel = new Label("Customer Details:");
            customerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label orderDetailsLabel = new Label("Order Details Items:");
            orderDetailsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            tablesSection.getChildren().addAll(
                    orderLabel, ordersTable,
                    customerLabel, customersTable,
                    orderDetailsLabel, orderDetailsTable);

            ordersContent.setTop(topContent);
            ordersContent.setCenter(tablesSection);
            contentArea.getChildren().add(ordersContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
