package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;

public class MainUI extends Application {
    private BorderPane root;
    private TableView<Object> tableView;
    private ObservableList<Object> currentData;
    private String currentTable = "Account";
    private VBox leftMenu;
    private FlowPane bottomPane;

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        // Top: Button kiểm tra và cập nhật dữ liệu
        Button checkBtn = new Button("Kiểm tra và Cập nhật dữ liệu");
        checkBtn.setOnAction(e -> checkAndUpdateData());
        VBox topBox = new VBox(checkBtn);
        topBox.setPadding(new Insets(10));
        root.setTop(topBox);

        // Left: Menu các bảng
        leftMenu = new VBox(10);
        leftMenu.setPadding(new Insets(10));
        String[] tables = { "Account", "Authors", "BookAuthor", "Books", "Categories", "Customers", "Languages",
                "OrderDetails", "Orders", "Publishers", "Staff", "Users" };
        for (String tbl : tables) {
            Button btn = new Button(tbl);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setOnAction(ev -> showTable(tbl));
            leftMenu.getChildren().add(btn);
        }
        root.setLeft(leftMenu);

        // Center: TableView động
        tableView = new TableView<>();
        showTable(currentTable);
        root.setCenter(tableView);

        // Bottom: FlowPane các Button tên bảng, màu ngẫu nhiên
        bottomPane = new FlowPane(10, 10);
        bottomPane.setPadding(new Insets(10));
        for (String tbl : tables) {
            Button btn = new Button(tbl);
            btn.setPrefSize(120, 120);
            btn.setStyle("-fx-background-color:" + getRandomColor());
            btn.setOnAction(ev -> showTable(tbl));
            bottomPane.getChildren().add(btn);
        }
        root.setBottom(bottomPane);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quản lý hiệu sách");
        primaryStage.show();
    }

    private void showTable(String tableName) {
        currentTable = tableName;
        tableView.getColumns().clear();
        // TODO: Tạo các cột TableColumn và load dữ liệu ObservableList cho từng bảng
        // Ví dụ: nếu tableName.equals("Books") thì tạo các cột bookId, title, ...
        // và gọi hàm loadBooksFromMongo()
    }

    private void checkAndUpdateData() {
        // TODO: Kiểm tra kết nối DB, lấy số lượng bản ghi bảng hiện tại
        // Alert thông báo kết quả
        // Nếu là Books, cập nhật storageQuantity nếu có OrderDetails > 0
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Kết quả");
        alert.setHeaderText(null);
        alert.setContentText("Số lượng bản ghi của bảng " + currentTable + ": ...");
        alert.showAndWait();
    }

    private String getRandomColor() {
        String[] colors = { "#e74c3c", "#27ae60", "#f1c40f", "#7f8c8d" };
        return colors[(int) (Math.random() * colors.length)];
    }

    public static void main(String[] args) {
        launch(args);
    }
}
