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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
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
        staffTable.setItems(loadStaffFromMongo());

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
        customerTable.setItems(loadCustomersFromMongo());

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
    private javafx.collections.ObservableList<StaffRow> loadStaffFromMongo() {
        javafx.collections.ObservableList<StaffRow> list = javafx.collections.FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> staffCol = db.getCollection("Staff");
            try (MongoCursor<Document> cursor = staffCol.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    System.out.println("[Staff] " + doc.toJson()); // Log document
                    String staffId = doc.getString("staffId");
                    String email = doc.containsKey("email") ? doc.getString("email") : "";
                    String password = doc.containsKey("password") ? doc.getString("password") : "";
                    String role = doc.containsKey("role") ? doc.getString("role") : "";
                    String fullName = doc.containsKey("fullName") ? doc.getString("fullName") : "";
                    String address = doc.containsKey("address") ? doc.getString("address") : "";
                    String phoneNumber = doc.containsKey("phoneNumber") ? doc.getString("phoneNumber") : "";
                    String position = doc.containsKey("position") ? doc.getString("position") : "";
                    list.add(new StaffRow(staffId, email, password, role, fullName, address, phoneNumber, position));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading staff from MongoDB: " + e.getMessage());
        }
        return list;
    }
    // Load Users from MongoDB
    private javafx.collections.ObservableList<UserRow> loadUsersFromMongo() {
        javafx.collections.ObservableList<UserRow> list = javafx.collections.FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> userCol = db.getCollection("Users");
            try (MongoCursor<Document> cursor = userCol.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String userId = doc.getString("userId");
                    String email = doc.containsKey("email") ? doc.getString("email") : "";
                    String password = doc.containsKey("password") ? doc.getString("password") : "";
                    String role = doc.containsKey("role") ? doc.getString("role") : "";
                    String fullName = doc.containsKey("fullName") ? doc.getString("fullName") : "";
                    String address = doc.containsKey("address") ? doc.getString("address") : "";
                    String phoneNumber = doc.containsKey("phoneNumber") ? doc.getString("phoneNumber") : "";
                    list.add(new UserRow(userId, email, password, role, fullName, address, phoneNumber));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading users from MongoDB: " + e.getMessage());
        }
        return list;
    }
    // Load Customers from MongoDB
    private javafx.collections.ObservableList<CustomerRow> loadCustomersFromMongo() {
        javafx.collections.ObservableList<CustomerRow> list = javafx.collections.FXCollections.observableArrayList();
        try {
            com.mongodb.client.MongoDatabase db = MongoDBConnection.getDatabase();
            com.mongodb.client.MongoCollection<org.bson.Document> col = db.getCollection("Customers");
            try (com.mongodb.client.MongoCursor<org.bson.Document> cursor = col.find().iterator()) {
                while (cursor.hasNext()) {
                    org.bson.Document doc = cursor.next();
                    System.out.println("[Customer] " + doc.toJson()); // Log document
                    String customerId = doc.getString("customerId");
                    String email = doc.containsKey("email") ? doc.getString("email") : "";
                    String password = doc.containsKey("password") ? doc.getString("password") : "";
                    String role = doc.containsKey("role") ? doc.getString("role") : "";
                    String fullName = doc.containsKey("fullName") ? doc.getString("fullName") : "";
                    String address = doc.containsKey("address") ? doc.getString("address") : "";
                    String phoneNumber = doc.containsKey("phoneNumber") ? doc.getString("phoneNumber") : "";
                    String rank = doc.containsKey("rank") ? doc.getString("rank") : "";
                    String spending = doc.containsKey("spending") ? doc.get("spending").toString() : "";
                    list.add(new CustomerRow(customerId, email, password, role, fullName, address, phoneNumber, rank, spending));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading customers from MongoDB: " + e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {
        launch(args);
    }
} 