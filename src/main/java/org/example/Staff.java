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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;

public class Staff extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane staffContent = createStaffContent();
        Scene scene = new Scene(staffContent, 800, 600);
        primaryStage.setTitle("Staff Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public BorderPane createStaffContent() {
        BorderPane staffContent = new BorderPane();

        VBox topContent = new VBox();
        Label titleLabel = new Label("Staff Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button createNewButton = new Button("Create New Staff");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");

        topContent.getChildren().addAll(titleLabel, createNewButton);
        topContent.setSpacing(10);
        topContent.setPadding(new Insets(10));

        // Tách giao diện thành 2 mục riêng: Quản lý Staff và Quản lý Customer
        // Mỗi mục là một TableView hiển thị đúng các trường đặc thù
        // Staff Table
        TableView<StaffRow> staffTable = new TableView<>();
        staffTable.setPrefHeight(250);
        TableColumn<StaffRow, String> staffIdColumn = new TableColumn<>("Staff ID");
        staffIdColumn.setCellValueFactory(cellData -> cellData.getValue().staffIdProperty());
        TableColumn<StaffRow, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        TableColumn<StaffRow, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        TableColumn<StaffRow, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        TableColumn<StaffRow, String> fullNameColumn = new TableColumn<>("Full Name");
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        TableColumn<StaffRow, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        TableColumn<StaffRow, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        TableColumn<StaffRow, String> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        staffTable.getColumns().setAll(staffIdColumn, emailColumn, passwordColumn, roleColumn, fullNameColumn, addressColumn, phoneNumberColumn, positionColumn);
        staffTable.setItems(loadStaffFromMySQL());

        // Customer Table
        TableView<CustomerRow> customerTable = new TableView<>();
        customerTable.setPrefHeight(250);
        TableColumn<CustomerRow, String> customerIdColumn = new TableColumn<>("Customer ID");
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty());
        TableColumn<CustomerRow, String> emailCusColumn = new TableColumn<>("Email");
        emailCusColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        TableColumn<CustomerRow, String> passwordCusColumn = new TableColumn<>("Password");
        passwordCusColumn.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        TableColumn<CustomerRow, String> roleCusColumn = new TableColumn<>("Role");
        roleCusColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        TableColumn<CustomerRow, String> fullNameCusColumn = new TableColumn<>("Full Name");
        fullNameCusColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        TableColumn<CustomerRow, String> addressCusColumn = new TableColumn<>("Address");
        addressCusColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        TableColumn<CustomerRow, String> phoneNumberCusColumn = new TableColumn<>("Phone Number");
        phoneNumberCusColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        TableColumn<CustomerRow, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty());
        TableColumn<CustomerRow, String> spendingColumn = new TableColumn<>("Spending");
        spendingColumn.setCellValueFactory(cellData -> cellData.getValue().spendingProperty());
        customerTable.getColumns().setAll(customerIdColumn, emailCusColumn, passwordCusColumn, roleCusColumn, fullNameCusColumn, addressCusColumn, phoneNumberCusColumn, rankColumn, spendingColumn);
        customerTable.setItems(loadCustomersFromMySQL());

        // Layout
        VBox staffBox = new VBox(10);
        staffBox.setPadding(new Insets(10));
        Label staffLabel = new Label("Staffs");
        staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        staffBox.getChildren().addAll(staffLabel, staffTable);

        VBox customerBox = new VBox(10);
        customerBox.setPadding(new Insets(10));
        Label customerLabel = new Label("Customers");
        customerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        customerBox.getChildren().addAll(customerLabel, customerTable);

        TabPane tabPane = new TabPane();
        Tab staffTab = new Tab("Staff");
        staffTab.setContent(staffBox);
        Tab customerTab = new Tab("Customer");
        customerTab.setContent(customerBox);
        tabPane.getTabs().addAll(staffTab, customerTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        staffContent.setTop(topContent);
        staffContent.setCenter(tabPane);
        return staffContent;
    }

    // Model for Staff Table
    public static class StaffRow {
        private javafx.beans.property.SimpleStringProperty staffId;
        private javafx.beans.property.SimpleStringProperty email;
        private javafx.beans.property.SimpleStringProperty password;
        private javafx.beans.property.SimpleStringProperty role;
        private javafx.beans.property.SimpleStringProperty fullName;
        private javafx.beans.property.SimpleStringProperty address;
        private javafx.beans.property.SimpleStringProperty phoneNumber;
        private javafx.beans.property.SimpleStringProperty position;
        public StaffRow(String staffId, String email, String password, String role, String fullName, String address, String phoneNumber, String position) {
            this.staffId = new javafx.beans.property.SimpleStringProperty(staffId);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.password = new javafx.beans.property.SimpleStringProperty(password);
            this.role = new javafx.beans.property.SimpleStringProperty(role);
            this.fullName = new javafx.beans.property.SimpleStringProperty(fullName);
            this.address = new javafx.beans.property.SimpleStringProperty(address);
            this.phoneNumber = new javafx.beans.property.SimpleStringProperty(phoneNumber);
            this.position = new javafx.beans.property.SimpleStringProperty(position);
        }
        public javafx.beans.property.SimpleStringProperty staffIdProperty() { return staffId; }
        public javafx.beans.property.SimpleStringProperty emailProperty() { return email; }
        public javafx.beans.property.SimpleStringProperty passwordProperty() { return password; }
        public javafx.beans.property.SimpleStringProperty roleProperty() { return role; }
        public javafx.beans.property.SimpleStringProperty fullNameProperty() { return fullName; }
        public javafx.beans.property.SimpleStringProperty addressProperty() { return address; }
        public javafx.beans.property.SimpleStringProperty phoneNumberProperty() { return phoneNumber; }
        public javafx.beans.property.SimpleStringProperty positionProperty() { return position; }
    }
    // Model for User Table
    public static class UserRow {
        private javafx.beans.property.SimpleStringProperty userId;
        private javafx.beans.property.SimpleStringProperty email;
        private javafx.beans.property.SimpleStringProperty password;
        private javafx.beans.property.SimpleStringProperty role;
        private javafx.beans.property.SimpleStringProperty fullName;
        private javafx.beans.property.SimpleStringProperty address;
        private javafx.beans.property.SimpleStringProperty phoneNumber;
        public UserRow(String userId, String email, String password, String role, String fullName, String address, String phoneNumber) {
            this.userId = new javafx.beans.property.SimpleStringProperty(userId);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.password = new javafx.beans.property.SimpleStringProperty(password);
            this.role = new javafx.beans.property.SimpleStringProperty(role);
            this.fullName = new javafx.beans.property.SimpleStringProperty(fullName);
            this.address = new javafx.beans.property.SimpleStringProperty(address);
            this.phoneNumber = new javafx.beans.property.SimpleStringProperty(phoneNumber);
        }
        public javafx.beans.property.SimpleStringProperty userIdProperty() { return userId; }
        public javafx.beans.property.SimpleStringProperty emailProperty() { return email; }
        public javafx.beans.property.SimpleStringProperty passwordProperty() { return password; }
        public javafx.beans.property.SimpleStringProperty roleProperty() { return role; }
        public javafx.beans.property.SimpleStringProperty fullNameProperty() { return fullName; }
        public javafx.beans.property.SimpleStringProperty addressProperty() { return address; }
        public javafx.beans.property.SimpleStringProperty phoneNumberProperty() { return phoneNumber; }
    }
    // Model for Customers Table
    public static class CustomerRow {
        private javafx.beans.property.SimpleStringProperty customerId, email, password, role, fullName, address, phoneNumber, rank, spending;
        public CustomerRow(String customerId, String email, String password, String role, String fullName, String address, String phoneNumber, String rank, String spending) {
            this.customerId = new javafx.beans.property.SimpleStringProperty(customerId);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
            this.password = new javafx.beans.property.SimpleStringProperty(password);
            this.role = new javafx.beans.property.SimpleStringProperty(role);
            this.fullName = new javafx.beans.property.SimpleStringProperty(fullName);
            this.address = new javafx.beans.property.SimpleStringProperty(address);
            this.phoneNumber = new javafx.beans.property.SimpleStringProperty(phoneNumber);
            this.rank = new javafx.beans.property.SimpleStringProperty(rank);
            this.spending = new javafx.beans.property.SimpleStringProperty(spending);
        }
        public javafx.beans.property.SimpleStringProperty customerIdProperty() { return customerId; }
        public javafx.beans.property.SimpleStringProperty emailProperty() { return email; }
        public javafx.beans.property.SimpleStringProperty passwordProperty() { return password; }
        public javafx.beans.property.SimpleStringProperty roleProperty() { return role; }
        public javafx.beans.property.SimpleStringProperty fullNameProperty() { return fullName; }
        public javafx.beans.property.SimpleStringProperty addressProperty() { return address; }
        public javafx.beans.property.SimpleStringProperty phoneNumberProperty() { return phoneNumber; }
        public javafx.beans.property.SimpleStringProperty rankProperty() { return rank; }
        public javafx.beans.property.SimpleStringProperty spendingProperty() { return spending; }
    }
    // Load Staff from MongoDB
    private javafx.collections.ObservableList<StaffRow> loadStaffFromMySQL() {
        javafx.collections.ObservableList<StaffRow> list = javafx.collections.FXCollections.observableArrayList();
        try {
            Connection conn = MySQLConnection.getConnection();
            String query = "SELECT staffId, email, password, role, fullName, address, phoneNumber, position FROM Staff";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String staffId = rs.getString("staffId");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                String fullName = rs.getString("fullName");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phoneNumber");
                String position = rs.getString("position");
                list.add(new StaffRow(staffId, email, password, role, fullName, address, phoneNumber, position));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error loading staff from MySQL: " + e.getMessage());
        }
        return list;
    }
    // Load Users from MongoDB
    private javafx.collections.ObservableList<UserRow> loadUsersFromMySQL() {
        javafx.collections.ObservableList<UserRow> list = javafx.collections.FXCollections.observableArrayList();
        try {
            Connection conn = MySQLConnection.getConnection();
            String query = "SELECT userId, email, password, role, fullName, address, phoneNumber FROM Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String userId = rs.getString("userId");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                String fullName = rs.getString("fullName");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phoneNumber");
                list.add(new UserRow(userId, email, password, role, fullName, address, phoneNumber));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error loading users from MySQL: " + e.getMessage());
        }
        return list;
    }
    // Load Customers from MongoDB
    private javafx.collections.ObservableList<CustomerRow> loadCustomersFromMySQL() {
        javafx.collections.ObservableList<CustomerRow> list = javafx.collections.FXCollections.observableArrayList();
        try {
            Connection conn = MySQLConnection.getConnection();
            String query = "SELECT customerId, email, password, role, fullName, address, phoneNumber, rank, spending FROM Customers";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String customerId = rs.getString("customerId");
                String email = rs.getString("email");
                String password = rs.getString("password");
                String role = rs.getString("role");
                String fullName = rs.getString("fullName");
                String address = rs.getString("address");
                String phoneNumber = rs.getString("phoneNumber");
                String rank = rs.getString("rank");
                String spending = rs.getString("spending");
                list.add(new CustomerRow(customerId, email, password, role, fullName, address, phoneNumber, rank, spending));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error loading customers from MySQL: " + e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 