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

public class ProductsUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(javafx.geometry.Pos.CENTER);

        Label titleLabel = new Label("Product Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button createNewButton = new Button("Create New Product");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewProductWindow());

        topSection.getChildren().addAll(titleLabel, createNewButton);
        root.setTop(topSection);

        // Publishers table
        TableView<Object> publishersTable = new TableView<>();
        publishersTable.setPrefHeight(150);

        TableColumn<Object, String> publisherIdColumn = new TableColumn<>("Publisher ID");
        TableColumn<Object, String> publisherNameColumn = new TableColumn<>("Publisher Name");
        TableColumn<Object, String> publishDateColumn = new TableColumn<>("Publish Date");
        TableColumn<Object, String> languageColumn = new TableColumn<>("Language");

        publishersTable.getColumns().add(publisherIdColumn);
        publishersTable.getColumns().add(publisherNameColumn);
        publishersTable.getColumns().add(publishDateColumn);
        publishersTable.getColumns().add(languageColumn);

        // Authors table
        TableView<Object> authorsTable = new TableView<>();
        authorsTable.setPrefHeight(150);

        TableColumn<Object, String> authorIdColumn = new TableColumn<>("Author ID");
        TableColumn<Object, String> authorNameColumn = new TableColumn<>("Author Name");
        TableColumn<Object, String> methodTypeColumn = new TableColumn<>("Method Type");

        authorsTable.getColumns().add(authorIdColumn);
        authorsTable.getColumns().add(authorNameColumn);
        authorsTable.getColumns().add(methodTypeColumn);

        // Books table
        TableView<Object> booksTable = new TableView<>();
        booksTable.setPrefHeight(150);

        TableColumn<Object, String> bookIdColumn = new TableColumn<>("Book ID");
        TableColumn<Object, String> bookTitleColumn = new TableColumn<>("Title");
        TableColumn<Object, String> descriptionColumn = new TableColumn<>("Description");
        TableColumn<Object, String> pageColumn = new TableColumn<>("Pages");
        TableColumn<Object, String> priceColumn = new TableColumn<>("Price");

        booksTable.getColumns().add(bookIdColumn);
        booksTable.getColumns().add(bookTitleColumn);
        booksTable.getColumns().add(descriptionColumn);
        booksTable.getColumns().add(pageColumn);
        booksTable.getColumns().add(priceColumn);

        // Add tables to layout
        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));

        Label publisherLabel = new Label("Publisher Details:");
        publisherLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label authorLabel = new Label("Author Details:");
        authorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label bookLabel = new Label("Book Details:");
        bookLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tablesSection.getChildren().addAll(
                publisherLabel, publishersTable,
                authorLabel, authorsTable,
                bookLabel, booksTable);

        root.setCenter(tablesSection);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Product Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateNewProductWindow() {
        Stage newWindow = new Stage();
        VBox newContent = new VBox(10);
        newContent.setPadding(new Insets(20));
        newContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label newWindowText = new Label("Create New Product");
        newWindowText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        newContent.getChildren().add(newWindowText);

        Scene newScene = new Scene(newContent, 400, 300);
        newWindow.setTitle("Create New Product");
        newWindow.setScene(newScene);
        newWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}