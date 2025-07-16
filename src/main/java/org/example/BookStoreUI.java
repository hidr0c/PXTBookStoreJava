package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import java.util.*;

public class BookStoreUI extends Application {
    private static final LinkedHashMap<String, List<String>> TABLES = new LinkedHashMap<>();
    static {
        TABLES.put("Account", Arrays.asList("accountId", "userId", "password", "email", "role"));
        TABLES.put("Authors", Arrays.asList("authorId", "nameAuthor"));
        TABLES.put("BookAuthor", Arrays.asList("authorId", "bookId"));
        TABLES.put("Books", Arrays.asList("bookId", "title", "description", "categoryId", "publisherId", "publishedDate", "language", "page", "imageURL", "price", "storageQuantity"));
        TABLES.put("Categories", Arrays.asList("categoryId", "nameCategory"));
        TABLES.put("Customers", Arrays.asList("customerId", "userId", "rank", "spending"));
        TABLES.put("Languages", Arrays.asList("languageId", "nameLanguage"));
        TABLES.put("Publishers", Arrays.asList("publisherId", "namePublisher", "publishedDate", "language", "page"));
        TABLES.put("Staff", Arrays.asList("staffId", "userId", "position"));
        TABLES.put("Suppliers", Arrays.asList("supplierId", "supplierName", "phone", "address"));
        TABLES.put("Users", Arrays.asList("userId", "fullName", "address", "phoneNumber"));
    }
    private BorderPane masterLayout;
    private TableView<Map<String, Object>> tableView;
    private String currentTable = "Books";
    private ObservableList<Map<String, Object>> currentData = FXCollections.observableArrayList();
    private MongoDatabase db;
    private Label lblTableTitle = new Label();
    private Label lblTableHint = new Label();
    private Button btnToStorage;

    @Override
    public void start(Stage primaryStage) {
        db = MongoDBConnection.getDatabase();
        masterLayout = new BorderPane();
        Button btnCheckUpdate = new Button("Kiểm tra và Cập nhật dữ liệu");
        btnCheckUpdate.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        btnCheckUpdate.setOnAction(e -> handleCheckUpdate());
        Button btnStats = new Button("Thống kê");
        btnStats.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        btnStats.setOnAction(e -> openStatsDialog());
        btnToStorage = new Button("Tại kho hàng");
        btnToStorage.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 16));
        btnToStorage.setOnAction(e -> switchTable("Books"));
        VBox layoutTop = new VBox(10, btnCheckUpdate, btnStats, btnToStorage);
        layoutTop.setPadding(new Insets(10));
        layoutTop.setAlignment(javafx.geometry.Pos.CENTER);
        masterLayout.setTop(layoutTop);
        VBox layoutLeft = new VBox(10);
        layoutLeft.setPadding(new Insets(10));
        for (String table : TABLES.keySet()) {
            Button btn = new Button(table);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(e -> switchTable(table));
            layoutLeft.getChildren().add(btn);
        }
        masterLayout.setLeft(layoutLeft);
        tableView = new TableView<>();
        tableView.setPrefHeight(400);
        lblTableTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        lblTableHint.setStyle("-fx-font-size: 13px; -fx-text-fill: #888;");
        VBox tableBox = new VBox(5, lblTableTitle, lblTableHint, tableView);
        tableBox.setPadding(new Insets(0,0,0,0));
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10));
        Button btnAdd = new Button("Thêm");
        Button btnEdit = new Button("Sửa");
        Button btnDelete = new Button("Xóa");
        actionBar.getChildren().addAll(btnAdd, btnEdit, btnDelete);
        VBox centerBox = new VBox(10, actionBar, tableBox);
        centerBox.setPadding(new Insets(10));
        masterLayout.setCenter(centerBox);
        FlowPane layoutBottom = new FlowPane();
        layoutBottom.setHgap(10);
        layoutBottom.setVgap(10);
        layoutBottom.setPadding(new Insets(10));
        String[] colors = {"#e74c3c", "#27ae60", "#f1c40f", "#7f8c8d"};
        Random rand = new Random();
        for (String table : TABLES.keySet()) {
            Button btn = new Button(table);
            btn.setPrefSize(120, 120);
            btn.setStyle("-fx-background-color: " + colors[rand.nextInt(colors.length)] + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            btn.setOnAction(e -> switchTable(table));
            layoutBottom.getChildren().add(btn);
        }
        masterLayout.setBottom(layoutBottom);
        Scene scene = new Scene(masterLayout, 900, 700);
        primaryStage.setTitle("Quản lý cửa hàng sách");
        primaryStage.setScene(scene);
        primaryStage.show();
        switchTable(currentTable);
        btnAdd.setOnAction(e -> openAddDialog(currentTable));
        btnEdit.setOnAction(e -> {
            Map<String, Object> selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) openEditDialog(currentTable, selected);
        });
        btnDelete.setOnAction(e -> {
            Map<String, Object> selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                deleteFromMongo(currentTable, selected);
                switchTable(currentTable);
            }
        });
    }

    private void switchTable(String table) {
        currentTable = table;
        tableView.getColumns().clear();
        List<String> cols = TABLES.get(table);
        for (String col : cols) {
            TableColumn<Map<String, Object>, String> tc = new TableColumn<>(col);
            tc.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getOrDefault(col, ""))));
            tableView.getColumns().add(tc);
        }
        currentData = loadTableData(table);
        tableView.setItems(currentData);
        lblTableTitle.setText("Quản lý " + getTableDisplayName(table));
        if (table.equals("Suppliers")) {
            lblTableHint.setText("Thêm, sửa, xóa nhà cung cấp. Mỗi nhà cung cấp gồm: supplierId, supplierName, phone, address.");
        } else if (table.equals("Authors")) {
            lblTableHint.setText("Thêm, sửa, xóa tác giả. Mỗi tác giả gồm: authorId, nameAuthor.");
        } else {
            lblTableHint.setText("");
        }
    }

    private String getTableDisplayName(String table) {
        if (table.equals("Suppliers")) return "Nhà cung cấp";
        if (table.equals("Authors")) return "Tác giả";
        if (table.equals("Books")) return "Sách";
        if (table.equals("Staff")) return "Nhân viên";
        if (table.equals("Users")) return "Người dùng";
        if (table.equals("Customers")) return "Khách hàng";
        if (table.equals("Categories")) return "Thể loại";
        if (table.equals("Publishers")) return "Nhà xuất bản";
        if (table.equals("Languages")) return "Ngôn ngữ";
        if (table.equals("Account")) return "Tài khoản";
        if (table.equals("BookAuthor")) return "Tác giả - Sách";
        return table;
    }

    private ObservableList<Map<String, Object>> loadTableData(String tableName) {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        if (db == null) return list;
        MongoCollection<Document> col = db.getCollection(tableName);
        try (MongoCursor<Document> cursor = col.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Map<String, Object> row = new HashMap<>();
                for (String key : TABLES.get(tableName)) row.put(key, doc.getOrDefault(key, ""));
                list.add(row);
            }
        }
        return list;
    }

    private void openAddDialog(String table) {
        Stage dialog = new Stage();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        List<String> cols = TABLES.get(table);
        List<Control> fields = new ArrayList<>();
        for (String col : cols) {
            Control input;
            if (col.toLowerCase().contains("date")) {
                input = new DatePicker();
            } else if (col.endsWith("Id") && !col.equalsIgnoreCase(table + "Id")) {
                ComboBox<String> combo = new ComboBox<>();
                ObservableList<Map<String, Object>> refData = loadTableData(getRefTable(col));
                for (Map<String, Object> row : refData) combo.getItems().add(String.valueOf(row.get(col)));
                input = combo;
            } else if (col.toLowerCase().contains("quantity") || col.toLowerCase().contains("price") || col.toLowerCase().contains("page")) {
                TextField tf = new TextField();
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("\\d*")) tf.setText(newVal.replaceAll("[^\\d]", ""));
                });
                input = tf;
            } else {
                input = new TextField();
            }
            fields.add(input);
            vbox.getChildren().add(new Label(col));
            vbox.getChildren().add(input);
        }
        Button btnSave = new Button("Lưu");
        btnSave.setOnAction(e -> {
            Document doc = new Document();
            for (int i = 0; i < cols.size(); i++) {
                String value;
                if (fields.get(i) instanceof TextField) value = ((TextField)fields.get(i)).getText();
                else if (fields.get(i) instanceof DatePicker) value = ((DatePicker)fields.get(i)).getValue() != null ? ((DatePicker)fields.get(i)).getValue().toString() : "";
                else if (fields.get(i) instanceof ComboBox) value = String.valueOf(((ComboBox<?>)fields.get(i)).getValue());
                else value = "";
                doc.put(cols.get(i), value);
            }
            try {
                db.getCollection(table).insertOne(doc);
                showAlert("Thêm thành công", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Thêm thất bại: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
            dialog.close();
            switchTable(table);
        });
        vbox.getChildren().add(btnSave);
        dialog.setScene(new Scene(vbox, 400, 50 + 40 * cols.size()));
        dialog.showAndWait();
    }

    private void openEditDialog(String table, Map<String, Object> selected) {
        Stage dialog = new Stage();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        List<String> cols = TABLES.get(table);
        List<Control> fields = new ArrayList<>();
        for (String col : cols) {
            Control input;
            if (col.toLowerCase().contains("date")) {
                DatePicker dp = new DatePicker();
                String val = String.valueOf(selected.getOrDefault(col, ""));
                if (!val.isEmpty()) try { dp.setValue(java.time.LocalDate.parse(val)); } catch (Exception ignore) {}
                input = dp;
            } else if (col.endsWith("Id") && !col.equalsIgnoreCase(table + "Id")) {
                ComboBox<String> combo = new ComboBox<>();
                ObservableList<Map<String, Object>> refData = loadTableData(getRefTable(col));
                for (Map<String, Object> row : refData) combo.getItems().add(String.valueOf(row.get(col)));
                combo.setValue(String.valueOf(selected.getOrDefault(col, "")));
                input = combo;
            } else if (col.toLowerCase().contains("quantity") || col.toLowerCase().contains("price") || col.toLowerCase().contains("page")) {
                TextField tf = new TextField(String.valueOf(selected.getOrDefault(col, "")));
                tf.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal.matches("\\d*")) tf.setText(newVal.replaceAll("[^\\d]", ""));
                });
                input = tf;
            } else {
                input = new TextField(String.valueOf(selected.getOrDefault(col, "")));
            }
            fields.add(input);
            vbox.getChildren().add(new Label(col));
            vbox.getChildren().add(input);
        }
        Button btnSave = new Button("Lưu");
        btnSave.setOnAction(e -> {
            Document filter = new Document();
            filter.put(cols.get(0), selected.get(cols.get(0)));
            Document update = new Document();
            for (int i = 0; i < cols.size(); i++) {
                String value;
                if (fields.get(i) instanceof TextField) value = ((TextField)fields.get(i)).getText();
                else if (fields.get(i) instanceof DatePicker) value = ((DatePicker)fields.get(i)).getValue() != null ? ((DatePicker)fields.get(i)).getValue().toString() : "";
                else if (fields.get(i) instanceof ComboBox) value = String.valueOf(((ComboBox<?>)fields.get(i)).getValue());
                else value = "";
                update.put(cols.get(i), value);
            }
            try {
                db.getCollection(table).updateOne(filter, new Document("$set", update));
                showAlert("Sửa thành công", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Sửa thất bại: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
            dialog.close();
            switchTable(table);
        });
        vbox.getChildren().add(btnSave);
        dialog.setScene(new Scene(vbox, 400, 50 + 40 * cols.size()));
        dialog.showAndWait();
    }

    private void deleteFromMongo(String table, Map<String, Object> selected) {
        List<String> cols = TABLES.get(table);
        Document filter = new Document();
        filter.put(cols.get(0), selected.get(cols.get(0)));
        try {
            db.getCollection(table).deleteOne(filter);
            showAlert("Xóa thành công", Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Xóa thất bại: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleCheckUpdate() {
        int count = currentData.size();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kết quả");
        alert.setHeaderText(null);
        alert.setContentText("Số lượng bản ghi bảng '" + currentTable + "': " + count);
        alert.showAndWait();
    }

    private void openStatsDialog() {
        Stage dialog = new Stage();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        Label lbl = new Label("Chọn loại thống kê:");
        ComboBox<String> cbType = new ComboBox<>();
        cbType.getItems().addAll("Ngày", "Tuần", "Tháng", "Năm");
        cbType.setValue("Ngày");
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());
        Button btnOK = new Button("Xem thống kê");
        vbox.getChildren().addAll(lbl, cbType, datePicker, btnOK);
        btnOK.setOnAction(ev -> {
            String type = cbType.getValue();
            java.time.LocalDate date = datePicker.getValue();
            showStats(type, date);
            dialog.close();
        });
        dialog.setScene(new Scene(vbox, 300, 180));
        dialog.showAndWait();
    }

    private void showStats(String type, java.time.LocalDate date) {
        if (db == null) return;
        MongoCollection<Document> col = db.getCollection("Orders");
        int count = 0;
        double total = 0;
        java.time.LocalDate start, end;
        if (type.equals("Ngày")) {
            start = date;
            end = date;
        } else if (type.equals("Tuần")) {
            start = date.with(java.time.DayOfWeek.MONDAY);
            end = date.with(java.time.DayOfWeek.SUNDAY);
        } else if (type.equals("Tháng")) {
            start = date.withDayOfMonth(1);
            end = date.withDayOfMonth(date.lengthOfMonth());
        } else {
            start = date.withDayOfYear(1);
            end = date.withDayOfYear(date.lengthOfYear());
        }
        try (MongoCursor<Document> cursor = col.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String orderDateStr = String.valueOf(doc.getOrDefault("orderDate", ""));
                java.time.LocalDate orderDate = null;
                try {
                    orderDate = java.time.LocalDate.parse(orderDateStr);
                } catch (Exception ex) {
                    continue;
                }
                if ((orderDate.isEqual(start) || orderDate.isAfter(start)) && (orderDate.isEqual(end) || orderDate.isBefore(end))) {
                    count++;
                    try {
                        total += Double.parseDouble(String.valueOf(doc.getOrDefault("total", "0")));
                    } catch (Exception ex) {}
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thống kê");
        alert.setHeaderText(null);
        alert.setContentText("Số lượng đơn: " + count + "\nTổng doanh thu: " + total);
        alert.showAndWait();
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Lỗi" : "Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private String getRefTable(String col) {
        if (col.equalsIgnoreCase("userId")) return "Users";
        if (col.equalsIgnoreCase("publisherId")) return "Publishers";
        if (col.equalsIgnoreCase("bookId")) return "Books";
        if (col.equalsIgnoreCase("authorId")) return "Authors";
        if (col.equalsIgnoreCase("customerId")) return "Customers";
        if (col.equalsIgnoreCase("staffId")) return "Staff";
        return col.replace("Id", "s");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
