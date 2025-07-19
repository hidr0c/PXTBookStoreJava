package org.example;

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
import java.sql.*;

public class LoginUI extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLogin();
    }

    public void showLogin() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        Text titleText = new Text("App quản lý cửa hàng bán sách");
        titleText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleText.setFill(Color.rgb(0, 102, 204));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        Label userLabel = new Label("Email:");
        TextField userField = new TextField();
        userField.setPromptText("Nhập email");
        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        Label passLabel = new Label("Mật khẩu:");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Nhập mật khẩu");
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #00994c; -fx-text-fill: white; -fx-font-weight: bold;");
        loginButton.setPrefWidth(200);

        root.getChildren().addAll(titleText, grid, loginButton);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Login button action
        loginButton.setOnAction(e -> {
            String email = userField.getText();
            String password = passField.getText();

            try {
                Connection conn = MySQLConnection.getConnection();
                if (conn == null) {
                    showError("Database connection failed!");
                    return;
                }
                String sql = "SELECT * FROM Accounts WHERE email=? AND pass=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, email);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    // Đăng nhập thành công
                    primaryStage.close();
                    Platform.runLater(() -> {
                        try {
                            new DashboardUI().start(new Stage());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    showError("Invalid credentials!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Database error!");
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
