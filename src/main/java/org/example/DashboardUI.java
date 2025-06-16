package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashboardUI extends Application {
    private Stage primaryStage;

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
        Button storageButton = createNavButton("Manage Storage");
        Button productButton = createNavButton("Manage Products");
        Button staffButton = createNavButton("Manage Staff");
        Button ordersButton = createNavButton("Manage Orders");

        drawer.getChildren().addAll(dashboardButton, storageButton, productButton, staffButton, ordersButton);

        // Create main content area
        StackPane contentArea = new StackPane();
        Text placeholderText = new Text("Select an option from the drawer");
        placeholderText.setFont(Font.font("Arial", 16));
        contentArea.getChildren().add(placeholderText);

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
            try {
                new ReportsUI().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
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
