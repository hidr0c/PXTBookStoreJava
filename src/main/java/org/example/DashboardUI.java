package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;<<<<<<<HEAD
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;=======
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;>>>>>>>b3fe551(uploading UI)
import javafx.stage.Stage;

public class DashboardUI extends Application {
    private Stage primaryStage;<<<<<<<HEAD=======
    private BorderPane mainLayout;>>>>>>>

    b3fe551 (uploading UI)

    private StackPane contentArea;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showDashboard();
    }

    public void showDashboard() {
        // Create main layout
        mainLayout = new BorderPane();

        // Create drawer (left side navigation)
        VBox drawer = new VBox(10);
        drawer.setPadding(new Insets(10));
        drawer.setStyle("-fx-background-color: #f0f0f0;");
        drawer.setPrefWidth(200);

        // Create navigation buttons
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
        // Create main content area
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));

        // Show dashboard content by default
        showDashboardContent();

        // Add components to main layout
        mainLayout.setLeft(drawer);
        mainLayout.setCenter(contentArea);

        // Create scene
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setTitle("Book Store Management System");
        primaryStage.setScene(scene);
        primaryStage.show(); // Add action listeners for navigation buttons using method references
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
            // Create the StorageUI content directly in the contentArea instead of opening a
            // new window
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

            storageContent.setTop(topContent);
            contentArea.getChildren().add(storageContent);

            // We're not actually calling the full start method of StorageUI but mimicking
            // its content
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

            productsContent.setTop(topContent);
            contentArea.getChildren().add(productsContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showStaffUI() {
        contentArea.getChildren().clear();

        try {
            // Create the StaffUI content directly in the contentArea
            BorderPane staffContent = new BorderPane();

            VBox topContent = new VBox();
            Label titleLabel = new Label("Staff Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Button createNewButton = new Button("Create New Staff");
            createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

            topContent.getChildren().addAll(titleLabel, createNewButton);
            topContent.setSpacing(10);
            topContent.setPadding(new Insets(10));

            staffContent.setTop(topContent);
            contentArea.getChildren().add(staffContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showOrdersUI() {
        contentArea.getChildren().clear();

        try {
            // Create the OrdersUI content directly in the contentArea
            BorderPane ordersContent = new BorderPane();

            VBox topContent = new VBox();
            Label titleLabel = new Label("Order Management");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

            Button createNewButton = new Button("Create New Order");
            createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

            topContent.getChildren().addAll(titleLabel, createNewButton);
            topContent.setSpacing(10);
            topContent.setPadding(new Insets(10));

            ordersContent.setTop(topContent);
            contentArea.getChildren().add(ordersContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
