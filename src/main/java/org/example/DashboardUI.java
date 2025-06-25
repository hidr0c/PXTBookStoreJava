package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardUI extends Application {
    private Stage primaryStage;
    private StackPane contentArea;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showDashboard();
    }

    public void showDashboard() {
        // Create main layout
        BorderPane mainLayout = new BorderPane();

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

        Button[] buttons = {dashboardButton, storageButton, productButton, staffButton, ordersButton};
        for (Button btn : buttons) {
            btn.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(btn, Priority.ALWAYS);
            btn.setPadding(new Insets(10));
        }

        drawer.getChildren().addAll(buttons);
        // Create main content area
        contentArea = new StackPane();
        // Set dashboard as the initial content
        contentArea.getChildren().add(DashboardStats.createDashboardContent());

        // Add components to main layout
        mainLayout.setLeft(drawer);
        mainLayout.setCenter(contentArea);

        // Create scene
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setTitle("Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add action listeners for navigation buttons
        dashboardButton.setOnAction(e -> {
            contentArea.getChildren().clear();
            contentArea.getChildren().add(DashboardStats.createDashboardContent());
        });
        storageButton.setOnAction(e -> {
            try {
                new StorageUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        productButton.setOnAction(e -> {
            try {
                new ProductsUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        staffButton.setOnAction(e -> {
            try {
                new StaffUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        ordersButton.setOnAction(e -> {
            try {
                new OrdersUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white; -fx-font-size: 14px;");
        return button;
    }
}
