package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
        // Khởi tạo cửa sổ chính
        this.primaryStage = primaryStage;
        // Hiển thị giao diện dashboard
        showDashboard();
    }

    public void showDashboard() {
        mainLayout = new BorderPane();
        VBox drawer = new VBox(10);
        drawer.setPadding(new Insets(20));
        drawer.setStyle("-fx-background-color: #f0f0f0;");
        drawer.setPrefWidth(200);
        // Sidebar thuần tiếng Việt, bổ sung các chức năng mới
        Button dashboardButton = new Button("Trang chủ");
        Button storageButton = new Button("Quản lý kho");
        Button productButton = new Button("Quản lý sản phẩm");
        Button staffButton = new Button("Quản lý nhân viên");
        Button supplierButton = new Button("Quản lý nhà cung cấp");
        Button authorButton = new Button("Quản lý tác giả");
        Button ordersButton = new Button("Quản lý hóa đơn");
        Button[] buttons = { dashboardButton, storageButton, productButton, staffButton, supplierButton, authorButton,
                ordersButton };
        for (Button btn : buttons) {
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPrefHeight(48);
            btn.setStyle(
                    "-fx-background-color: #0066cc; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px;");
            btn.setOnMouseEntered(e -> btn.setStyle(
                    "-fx-background-color: #3399ff; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px;"));
            btn.setOnMouseExited(e -> btn.setStyle(
                    "-fx-background-color: #0066cc; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px;"));
        }
        drawer.getChildren().addAll(buttons);

        // Tạo vùng hiển thị nội dung
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(20));
        showDashboardContent();

        mainLayout.setLeft(drawer);
        mainLayout.setCenter(contentArea);

        // Tạo scene và hiển thị
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setTitle("Hệ thống quản lý cửa hàng sách");
        primaryStage.setScene(scene);
        primaryStage.show(); // Add action listeners for navigation buttons
        dashboardButton.setOnAction(e -> showDashboardContent());
        storageButton.setOnAction(e -> showStorageUI());
        productButton.setOnAction(e -> showProductsUI());
        staffButton.setOnAction(e -> showStaffUI());
        ordersButton.setOnAction(e -> showOrdersUI());
    }

    private void showDashboardContent() {
        contentArea.getChildren().clear();

        // Create a VBox to hold all dashboard content
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Welcome to the Book Store Management System");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox dashboardStatsContent = DashboardStats.createDashboardContent();

        // 2. Add the remaining parts of the existing dashboard (welcome label, tables,
        // etc.)

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

        dashboardContent.getChildren().addAll(
                welcomeLabel,
                dashboardStatsContent,
                recentOrdersLabel, recentOrdersTable,
                lowStockLabel, lowStockTable);

        // Wrap the dashboardContent VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);

        contentArea.getChildren().add(scrollPane);
    }

    private void showStorageUI() {
        contentArea.getChildren().clear();

        // Create storage content using the Storage class
        Storage storage = new Storage();
        BorderPane storageContent = storage.createStorageContent();

        contentArea.getChildren().add(storageContent);
    }

    private void showProductsUI() {
        contentArea.getChildren().clear();

        // Create product content using the Product class
        Product product = new Product();
        BorderPane productContent = product.createProductContent();

        contentArea.getChildren().add(productContent);
    }

    private void showStaffUI() {
        contentArea.getChildren().clear();

        // Create staff content using the Staff class
        Staff staff = new Staff();
        BorderPane staffContent = staff.createStaffContent();

        contentArea.getChildren().add(staffContent);
    }

    private void showOrdersUI() {
        contentArea.getChildren().clear();

        // Create order content using the Order class
        Order order = new Order();
        BorderPane orderContent = order.createOrderContent();

        contentArea.getChildren().add(orderContent);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
