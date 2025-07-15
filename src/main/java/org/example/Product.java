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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;

public class Product extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        BorderPane productContent = createProductContent();
        Scene scene = new Scene(productContent, 800, 600);
        primaryStage.setTitle("Product Management");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public BorderPane createProductContent() {
        BorderPane productsContent = new BorderPane();

        VBox topContent = new VBox();
        Label titleLabel = new Label("Product Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button createNewButton = new Button("Create New Product");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        topContent.getChildren().addAll(titleLabel, createNewButton);
        topContent.setSpacing(10);
        topContent.setPadding(new Insets(10));

        // Books Table
        TableView<BookRow> booksTable = new TableView<>();
        booksTable.setPrefHeight(400);
        TableColumn<BookRow, String> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        TableColumn<BookRow, String> bookTitleColumn = new TableColumn<>("Title");
        bookTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        TableColumn<BookRow, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        TableColumn<BookRow, String> pageColumn = new TableColumn<>("Pages");
        pageColumn.setCellValueFactory(cellData -> cellData.getValue().pagesProperty());
        TableColumn<BookRow, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        booksTable.getColumns().addAll(bookIdColumn, bookTitleColumn, descriptionColumn, pageColumn, priceColumn);
        booksTable.setItems(loadBooksFromMongo());
        // CRUD buttons for Books Table
        Button addBookBtn = new Button("Thêm Book");
        Button editBookBtn = new Button("Sửa Book");
        Button deleteBookBtn = new Button("Xóa Book");
        editBookBtn.setDisable(true);
        deleteBookBtn.setDisable(true);
        HBox bookBtnBox = new HBox(10, addBookBtn, editBookBtn, deleteBookBtn);
        bookBtnBox.setPadding(new Insets(0, 0, 10, 0));
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean selected = newSel != null;
            editBookBtn.setDisable(!selected);
            deleteBookBtn.setDisable(!selected);
        });

        VBox tablesSection = new VBox(20);
        tablesSection.setPadding(new Insets(10));
        Label bookLabel = new Label("Book Details:");
        bookLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        tablesSection.getChildren().addAll(bookLabel, bookBtnBox, booksTable);

        productsContent.setTop(topContent);
        productsContent.setCenter(tablesSection);
        return productsContent;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Model for Publishers Table
    public static class PublisherRow {
        private SimpleStringProperty publisherId, publisherName, publishDate, language;
        public PublisherRow(String publisherId, String publisherName, String publishDate, String language) {
            this.publisherId = new SimpleStringProperty(publisherId);
            this.publisherName = new SimpleStringProperty(publisherName);
            this.publishDate = new SimpleStringProperty(publishDate);
            this.language = new SimpleStringProperty(language);
        }
        public SimpleStringProperty publisherIdProperty() { return publisherId; }
        public SimpleStringProperty publisherNameProperty() { return publisherName; }
        public SimpleStringProperty publishDateProperty() { return publishDate; }
        public SimpleStringProperty languageProperty() { return language; }
    }
    // Model for Authors Table
    public static class AuthorRow {
        private SimpleStringProperty authorId, authorName, methodType;
        public AuthorRow(String authorId, String authorName, String methodType) {
            this.authorId = new SimpleStringProperty(authorId);
            this.authorName = new SimpleStringProperty(authorName);
            this.methodType = new SimpleStringProperty(methodType);
        }
        public SimpleStringProperty authorIdProperty() { return authorId; }
        public SimpleStringProperty authorNameProperty() { return authorName; }
        public SimpleStringProperty methodTypeProperty() { return methodType; }
    }
    // Model for Books Table
    public static class BookRow {
        private SimpleStringProperty bookId, title, description, pages, price;
        public BookRow(String bookId, String title, String description, String pages, String price) {
            this.bookId = new SimpleStringProperty(bookId);
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty(description);
            this.pages = new SimpleStringProperty(pages);
            this.price = new SimpleStringProperty(price);
        }
        public SimpleStringProperty bookIdProperty() { return bookId; }
        public SimpleStringProperty titleProperty() { return title; }
        public SimpleStringProperty descriptionProperty() { return description; }
        public SimpleStringProperty pagesProperty() { return pages; }
        public SimpleStringProperty priceProperty() { return price; }
    }
    // Load Publishers from MongoDB
    private ObservableList<PublisherRow> loadPublishersFromMongo() {
        ObservableList<PublisherRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> col = db.getCollection("Publishers");
            try (MongoCursor<Document> cursor = col.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String publisherId = doc.getString("publisherId");
                    String publisherName = doc.containsKey("publisherName") ? doc.getString("publisherName") : "";
                    String publishDate = doc.containsKey("publishDate") ? doc.getString("publishDate") : "";
                    String language = doc.containsKey("language") ? doc.getString("language") : "";
                    list.add(new PublisherRow(publisherId, publisherName, publishDate, language));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading publishers from MongoDB: " + e.getMessage());
        }
        return list;
    }
    // Load Authors from MongoDB
    private ObservableList<AuthorRow> loadAuthorsFromMongo() {
        ObservableList<AuthorRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> col = db.getCollection("Authors");
            try (MongoCursor<Document> cursor = col.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String authorId = doc.getString("authorId");
                    String authorName = doc.containsKey("authorName") ? doc.getString("authorName") : "";
                    String methodType = doc.containsKey("methodType") ? doc.getString("methodType") : "";
                    list.add(new AuthorRow(authorId, authorName, methodType));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading authors from MongoDB: " + e.getMessage());
        }
        return list;
    }
    // Load Books from MongoDB
    private ObservableList<BookRow> loadBooksFromMongo() {
        ObservableList<BookRow> list = FXCollections.observableArrayList();
        try {
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> col = db.getCollection("Books");
            try (MongoCursor<Document> cursor = col.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String bookId = doc.getString("bookId");
                    String title = doc.containsKey("title") ? doc.getString("title") : "";
                    String description = doc.containsKey("description") ? doc.getString("description") : "";
                    String pages = doc.containsKey("pages") ? doc.get("pages").toString() : "";
                    String price = doc.containsKey("price") ? doc.get("price").toString() : "";
                    list.add(new BookRow(bookId, title, description, pages, price));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading books from MongoDB: " + e.getMessage());
        }
        return list;
    }
} 