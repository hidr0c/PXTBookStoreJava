package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
        Button staffButton = createNavButton("Quản lý user");
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
        primaryStage.setTitle("Hệ thống quản lý cửa hàng sách");
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

        // Create a VBox to hold all dashboard content
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(10));

        Label welcomeLabel = new Label("Welcome to the Book Store Management System");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // XÓA hoặc COMMENT tất cả các dòng liên quan đến DashboardStats (import, gọi hàm, biến, ...)
        // VBox dashboardStatsContent = DashboardStats.createDashboardContent();

        dashboardContent.getChildren().addAll(
                welcomeLabel
                // đã xóa dashboardStatsContent
        );

        // Wrap the dashboardContent VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);

        contentArea.getChildren().add(scrollPane);
    }

    private void showStorageUI() {
        contentArea.getChildren().clear();
        BorderPane storageContent = Storage.createStorageContent();
        contentArea.getChildren().add(storageContent);
    }

    private void showProductsUI() {
        contentArea.getChildren().clear();
        BorderPane productContent = Product.createProductContent();
        contentArea.getChildren().add(productContent);
    }

    private void showStaffUI() {
        contentArea.getChildren().clear();
        BorderPane userContent = UserUI.createUserContent();
        contentArea.getChildren().add(userContent);
    }

    private void showOrdersUI() {
        contentArea.getChildren().clear();
        BorderPane orderContent = Order.createOrderContent();
        contentArea.getChildren().add(orderContent);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
