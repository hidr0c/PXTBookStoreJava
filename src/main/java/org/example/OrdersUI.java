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

public class OrdersUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Order Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button createNewButton = new Button("Create New Order");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewOrderWindow());

        topSection.getChildren().addAll(titleLabel, createNewButton);
        root.setTop(topSection);

        // Orders table
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

        // Customers table (related to Orders)
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

        // Order Details table (related to Orders)
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

        // Add tables to layout
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

        root.setCenter(tablesSection);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Order Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateNewOrderWindow() {
        Stage newWindow = new Stage();
        VBox newContent = new VBox(10);
        newContent.setPadding(new Insets(20));
        newContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label newWindowText = new Label("Create New Order");
        newWindowText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        newContent.getChildren().add(newWindowText);

        Scene newScene = new Scene(newContent, 400, 300);
        newWindow.setTitle("Create New Order");
        newWindow.setScene(newScene);
        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}