package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;

public class LoginUI extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        // Khởi tạo cửa sổ chính
        this.primaryStage = primaryStage;
        // Hiển thị giao diện đăng nhập
        showLogin();
    }

    public void showLogin() {
        // Tạo layout chính dạng VBox, cách đều 20px
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        // Tạo tiêu đề
        Text titleText = new Text("App quản lý cửa hàng bán sách");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleText.setFill(Color.rgb(0, 102, 204));

        // Tạo lưới nhập liệu
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Tạo label và textfield cho email
        Label userLabel = new Label("Email:");
        TextField userField = new TextField();
        userField.setPromptText("Nhập email");
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);

        // Tạo label và passwordfield cho mật khẩu
        Label passLabel = new Label("Mật khẩu:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Nhập mật khẩu");
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);

        // Tạo nút đăng nhập
        Button loginButton = new Button("Đăng nhập");
        loginButton.setStyle("-fx-background-color: #00994c; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setPrefWidth(200);

        // Thêm các thành phần vào layout chính
        root.getChildren().addAll(titleText, grid, loginButton);

        // Tạo scene và hiển thị
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Login button action
        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            System.out.println("Attempting login with:");
            System.out.println("  Username (email): " + username);
            System.out.println("  Password: " + password);

            MongoDatabase database = MongoDBConnection.getDatabase();
            if (database == null) {
                System.out.println("Database connection is null. Cannot proceed with login.");
                showError("Database connection failed!");
                return;
            }

            MongoCollection<Document> usersCollection = database.getCollection("Account");

            System.out.println("--- All users in 'users' collection ---");
            for (Document doc : usersCollection.find()) {
                System.out.println(doc.toJson());
            }
            System.out.println("---------------------------------------");

            Document query = new Document("email", username).append("password", password);
            System.out.println("  MongoDB Query: " + query.toJson());

            Document user = usersCollection.find(query).first();

            if (user != null) {
                System.out.println("Login successful for user: " + username);
                primaryStage.close();
                Platform.runLater(() -> {
                    try {
                        new DashboardUI().start(new Stage());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            } else {
                System.out.println("Login failed: Invalid credentials for user: " + username);
                showError("Invalid credentials!");
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
