package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class StaffUI extends Application {

    // Sample data for demonstration
    private ObservableList<Staff> staffData = FXCollections.observableArrayList(
            new Staff("STF001", "Store Manager", "Full-time", "Active"),
            new Staff("STF002", "Assistant Manager", "Full-time", "Active"),
            new Staff("STF003", "Senior Bookseller", "Full-time", "Active"),
            new Staff("STF004", "Junior Bookseller", "Part-time", "Active"),
            new Staff("STF005", "Cashier", "Part-time", "On Leave"));

    private ObservableList<User> userData = FXCollections.observableArrayList(
            new User("USR001", "John Smith", "123 Main Street, Boston", "617-555-0123", "john.smith@email.com"),
            new User("USR002", "Emily Johnson", "456 Oak Avenue, Cambridge", "617-555-0124", "emily.j@email.com"),
            new User("USR003", "Michael Davis", "789 Pine Road, Somerville", "617-555-0125", "michael.d@email.com"),
            new User("USR004", "Sarah Wilson", "101 Elm Street, Brookline", "617-555-0126", "sarah.w@email.com"),
            new User("USR005", "David Thompson", "202 Maple Drive, Medford", "617-555-0127", "david.t@email.com"));

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Quản lý nhân viên");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Search and buttons section
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm nhân viên...");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;");
        Button createNewButton = new Button("Thêm nhân viên mới");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewStaffWindow());

        actionBar.getChildren().addAll(searchField, searchButton, createNewButton);

        // Stats overview
        HBox statsBar = new HBox(20);
        statsBar.setPadding(new Insets(10));
        statsBar.setAlignment(Pos.CENTER);

        VBox totalStaffStats = createStatBox("Tổng nhân viên", "5");
        VBox activeStaffStats = createStatBox("Đang làm việc", "4");
        VBox fullTimeStats = createStatBox("Toàn thời gian", "3");
        VBox partTimeStats = createStatBox("Bán thời gian", "2");

        statsBar.getChildren().addAll(totalStaffStats, activeStaffStats, fullTimeStats, partTimeStats);

        topSection.getChildren().addAll(titleLabel, actionBar, statsBar);
        root.setTop(topSection);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Staff tab with table
        Tab staffTab = new Tab("Nhân viên");
        VBox staffBox = new VBox(10);
        staffBox.setPadding(new Insets(10));

        HBox staffHeader = new HBox(10);
        Label staffLabel = new Label("Danh sách nhân viên");
        staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button exportStaffBtn = new Button("Xuất danh sách");
        exportStaffBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        staffHeader.getChildren().addAll(staffLabel, new Pane(), exportStaffBtn);
        HBox.setHgrow(new Pane(), Priority.ALWAYS);

        // Staff table
        TableView<Staff> staffTable = new TableView<>();
        staffTable.setPrefHeight(250);
        staffTable.setItems(staffData);

        TableColumn<Staff, String> staffIdColumn = new TableColumn<>("Mã nhân viên");
        staffIdColumn.setCellValueFactory(cellData -> cellData.getValue().staffIdProperty());
        staffIdColumn.setPrefWidth(100);

        TableColumn<Staff, String> positionColumn = new TableColumn<>("Chức vụ");
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());
        positionColumn.setPrefWidth(150);

        TableColumn<Staff, String> employmentTypeColumn = new TableColumn<>("Employment Type");
        employmentTypeColumn.setCellValueFactory(cellData -> cellData.getValue().employmentTypeProperty());
        employmentTypeColumn.setPrefWidth(120);

        TableColumn<Staff, String> statusColumn = new TableColumn<>("Trạng thái");
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusColumn.setPrefWidth(100);

        TableColumn<Staff, Void> actionColumn = new TableColumn<>("Thao tác");
        actionColumn.setPrefWidth(180);
        actionColumn.setCellFactory(param -> new TableCell<Staff, Void>() {
            private final Button editBtn = new Button("Sửa");
            private final Button deleteBtn = new Button("Xóa");
            private final Button statusBtn = new Button("Đổi trạng thái");

            {
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                statusBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");

                editBtn.setOnAction(event -> {
                    Staff data = getTableView().getItems().get(getIndex());
                    // Edit functionality here
                    System.out.println("Edit button clicked for: " + data.getStaffId());
                });

                deleteBtn.setOnAction(event -> {
                    Staff data = getTableView().getItems().get(getIndex());
                    // Delete functionality here
                    System.out.println("Delete button clicked for: " + data.getStaffId());
                });

                statusBtn.setOnAction(event -> {
                    Staff data = getTableView().getItems().get(getIndex());
                    // Status change functionality
                    System.out.println("Status button clicked for: " + data.getStaffId());
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
                    pane.getChildren().addAll(editBtn, deleteBtn, statusBtn);
                    setGraphic(pane);
                }
            }
        }); // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        staffTable.getColumns().add(staffIdColumn);
        staffTable.getColumns().add(positionColumn);
        staffTable.getColumns().add(employmentTypeColumn);
        staffTable.getColumns().add(statusColumn);
        staffTable.getColumns().add(actionColumn);

        staffBox.getChildren().addAll(staffHeader, staffTable);
        staffTab.setContent(staffBox);

        // User tab with table
        Tab userTab = new Tab("Thông tin người dùng");
        VBox userBox = new VBox(10);
        userBox.setPadding(new Insets(10));
        Label userLabel = new Label("Danh sách người dùng");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // User table
        TableView<User> userTable = new TableView<>();
        userTable.setPrefHeight(250);
        userTable.setItems(userData);

        TableColumn<User, String> userIdColumn = new TableColumn<>("ID");
        userIdColumn.setCellValueFactory(cellData -> cellData.getValue().userIdProperty());
        userIdColumn.setPrefWidth(80);

        TableColumn<User, String> fullNameColumn = new TableColumn<>("Họ và tên");
        fullNameColumn.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        fullNameColumn.setPrefWidth(150);

        TableColumn<User, String> addressColumn = new TableColumn<>("Địa chỉ");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        addressColumn.setPrefWidth(200);

        TableColumn<User, String> phoneNumberColumn = new TableColumn<>("Số điện thoại");
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        phoneNumberColumn.setPrefWidth(120);

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setPrefWidth(180);

        TableColumn<User, Void> userActionColumn = new TableColumn<>("Thao tác");
        userActionColumn.setPrefWidth(120);
        userActionColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editBtn = new Button("Sửa");
            private final Button deleteBtn = new Button("Xóa");

            {
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    User data = getTableView().getItems().get(getIndex());
                    // Edit functionality here
                    System.out.println("Edit button clicked for: " + data.getFullName());
                });

                deleteBtn.setOnAction(event -> {
                    User data = getTableView().getItems().get(getIndex());
                    // Delete functionality here
                    System.out.println("Delete button clicked for: " + data.getFullName());
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
                    pane.getChildren().addAll(editBtn, deleteBtn);
                    setGraphic(pane);
                }
            }
        }); // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        userTable.getColumns().add(userIdColumn);
        userTable.getColumns().add(fullNameColumn);
        userTable.getColumns().add(addressColumn);
        userTable.getColumns().add(phoneNumberColumn);
        userTable.getColumns().add(emailColumn);
        userTable.getColumns().add(userActionColumn);

        userBox.getChildren().addAll(userLabel, userTable);
        userTab.setContent(userBox);

        // Attendance tab
        Tab attendanceTab = new Tab("Chuyên cần");
        VBox attendanceBox = createPlaceholderContent("Dữ liệu chấm công sẽ được hiển thị ở đây");
        attendanceTab.setContent(attendanceBox);

        // Performance tab
        Tab performanceTab = new Tab("Hiệu suất");
        VBox performanceBox = createPlaceholderContent("Thông số hiệu suất nhân viên sẽ được hiển thị ở đây");
        performanceTab.setContent(performanceBox);

        // Thêm từng tab riêng lẻ để tránh cảnh báo type safety
        tabPane.getTabs().add(staffTab);
        tabPane.getTabs().add(userTab);
        tabPane.getTabs().add(attendanceTab);
        tabPane.getTabs().add(performanceTab);
        root.setCenter(tabPane); // Status bar at the bottom
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #f8f9fa;");
        Label statusLabel = new Label("Sẵn sàng");
        statusBar.getChildren().add(statusLabel);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Quản lý hiệu sách - Nhân viên");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createPlaceholderContent(String message) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label placeholderLabel = new Label(message);
        placeholderLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #999999;");

        box.getChildren().add(placeholderLabel);
        return box;
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

    private void showCreateNewStaffWindow() {
        Stage newWindow = new Stage();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Staff Tab
        Tab staffTab = new Tab("Nhân viên mới");
        VBox staffForm = new VBox(10);
        staffForm.setPadding(new Insets(20));

        Label staffTitle = new Label("Thêm nhân viên mới");
        staffTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane staffGrid = new GridPane();
        staffGrid.setHgap(10);
        staffGrid.setVgap(10);
        staffGrid.setPadding(new Insets(10));

        staffGrid.add(new Label("Mã nhân viên:"), 0, 0);
        TextField staffIdField = new TextField();
        staffIdField.setPromptText("Tự động tạo");
        staffIdField.setDisable(true);
        staffGrid.add(staffIdField, 1, 0);

        staffGrid.add(new Label("Chức vụ:"), 0, 1);
        TextField positionField = new TextField();
        staffGrid.add(positionField, 1, 1);

        staffGrid.add(new Label("Loại hợp đồng:"), 0, 2);
        ComboBox<String> employmentTypeCombo = new ComboBox<>();
        employmentTypeCombo.getItems().addAll("Toàn thời gian", "Bán thời gian", "Hợp đồng", "Thời vụ");
        employmentTypeCombo.setValue("Toàn thời gian");
        staffGrid.add(employmentTypeCombo, 1, 2);

        staffGrid.add(new Label("Trạng thái:"), 0, 3);
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Đang làm việc", "Nghỉ phép", "Đã nghỉ việc");
        statusCombo.setValue("Đang làm việc");
        staffGrid.add(statusCombo, 1, 3);

        staffGrid.add(new Label("Người dùng:"), 0, 4);
        ComboBox<String> userCombo = new ComboBox<>();
        userCombo.getItems().addAll("John Smith", "Emily Johnson", "Michael Davis", "Sarah Wilson", "David Thompson");
        staffGrid.add(userCombo, 1, 4);

        HBox staffButtons = new HBox(10);
        Button saveStaffButton = new Button("Lưu nhân viên");
        saveStaffButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelStaffButton = new Button("Hủy");
        staffButtons.getChildren().addAll(saveStaffButton, cancelStaffButton);
        staffButtons.setAlignment(Pos.CENTER_RIGHT);
        staffForm.getChildren().addAll(staffTitle, staffGrid, staffButtons);
        staffTab.setContent(staffForm);

        // User Tab
        Tab userTab = new Tab("Người dùng mới");
        VBox userForm = new VBox(10);
        userForm.setPadding(new Insets(20));

        Label userTitle = new Label("Thêm người dùng mới");
        userTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane userGrid = new GridPane();
        userGrid.setHgap(10);
        userGrid.setVgap(10);
        userGrid.setPadding(new Insets(10));

        userGrid.add(new Label("ID người dùng:"), 0, 0);
        TextField userIdField = new TextField();
        userIdField.setPromptText("Tự động tạo");
        userIdField.setDisable(true);
        userGrid.add(userIdField, 1, 0);

        userGrid.add(new Label("Họ và tên:"), 0, 1);
        TextField fullNameField = new TextField();
        userGrid.add(fullNameField, 1, 1);

        userGrid.add(new Label("Địa chỉ:"), 0, 2);
        TextField addressField = new TextField();
        userGrid.add(addressField, 1, 2);

        userGrid.add(new Label("Số điện thoại:"), 0, 3);
        TextField phoneField = new TextField();
        userGrid.add(phoneField, 1, 3);

        userGrid.add(new Label("Email:"), 0, 4);
        TextField emailField = new TextField();
        userGrid.add(emailField, 1, 4);

        HBox userButtons = new HBox(10);
        Button saveUserButton = new Button("Lưu người dùng");
        saveUserButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelUserButton = new Button("Hủy");
        userButtons.getChildren().addAll(saveUserButton, cancelUserButton);
        userButtons.setAlignment(Pos.CENTER_RIGHT);
        userForm.getChildren().addAll(userTitle, userGrid, userButtons);
        userTab.setContent(userForm);

        // Thêm từng tab riêng lẻ để tránh cảnh báo type safety
        tabPane.getTabs().add(staffTab);
        tabPane.getTabs().add(userTab);

        Scene newScene = new Scene(tabPane, 500, 500);
        newWindow.setTitle("Thêm nhân viên mới");
        newWindow.setScene(newScene);
        newWindow.show(); // Action handlers
        cancelStaffButton.setOnAction(e -> newWindow.close());
        cancelUserButton.setOnAction(e -> newWindow.close());

        saveStaffButton.setOnAction(e -> {
            // Save staff logic here
            System.out.println(
                    "Đang lưu nhân viên mới: " + positionField.getText() + " (" + employmentTypeCombo.getValue() + ")");
            newWindow.close();
        });

        saveUserButton.setOnAction(e -> {
            // Save user logic here
            System.out.println("Đang lưu người dùng mới: " + fullNameField.getText());
            newWindow.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Model classes
    public static class Staff {
        private javafx.beans.property.SimpleStringProperty staffId;
        private javafx.beans.property.SimpleStringProperty position;
        private javafx.beans.property.SimpleStringProperty employmentType;
        private javafx.beans.property.SimpleStringProperty status;

        public Staff(String id, String position, String employmentType, String status) {
            this.staffId = new javafx.beans.property.SimpleStringProperty(id);
            this.position = new javafx.beans.property.SimpleStringProperty(position);
            this.employmentType = new javafx.beans.property.SimpleStringProperty(employmentType);
            this.status = new javafx.beans.property.SimpleStringProperty(status);
        }

        public String getStaffId() {
            return staffId.get();
        }

        public javafx.beans.property.SimpleStringProperty staffIdProperty() {
            return staffId;
        }

        public String getPosition() {
            return position.get();
        }

        public javafx.beans.property.SimpleStringProperty positionProperty() {
            return position;
        }

        public String getEmploymentType() {
            return employmentType.get();
        }

        public javafx.beans.property.SimpleStringProperty employmentTypeProperty() {
            return employmentType;
        }

        public String getStatus() {
            return status.get();
        }

        public javafx.beans.property.SimpleStringProperty statusProperty() {
            return status;
        }
    }

    public static class User {
        private javafx.beans.property.SimpleStringProperty userId;
        private javafx.beans.property.SimpleStringProperty fullName;
        private javafx.beans.property.SimpleStringProperty address;
        private javafx.beans.property.SimpleStringProperty phoneNumber;
        private javafx.beans.property.SimpleStringProperty email;

        public User(String id, String name, String address, String phone, String email) {
            this.userId = new javafx.beans.property.SimpleStringProperty(id);
            this.fullName = new javafx.beans.property.SimpleStringProperty(name);
            this.address = new javafx.beans.property.SimpleStringProperty(address);
            this.phoneNumber = new javafx.beans.property.SimpleStringProperty(phone);
            this.email = new javafx.beans.property.SimpleStringProperty(email);
        }

        public String getUserId() {
            return userId.get();
        }

        public javafx.beans.property.SimpleStringProperty userIdProperty() {
            return userId;
        }

        public String getFullName() {
            return fullName.get();
        }

        public javafx.beans.property.SimpleStringProperty fullNameProperty() {
            return fullName;
        }

        public String getAddress() {
            return address.get();
        }

        public javafx.beans.property.SimpleStringProperty addressProperty() {
            return address;
        }

        public String getPhoneNumber() {
            return phoneNumber.get();
        }

        public javafx.beans.property.SimpleStringProperty phoneNumberProperty() {
            return phoneNumber;
        }

        public String getEmail() {
            return email.get();
        }

        public javafx.beans.property.SimpleStringProperty emailProperty() {
            return email;
        }
    }
}