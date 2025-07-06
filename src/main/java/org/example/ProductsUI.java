package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProductsUI extends Application {

    private ObservableList<Publisher> publisherData = FXCollections.observableArrayList(
            new Publisher("PUB001", "Penguin Random House", "1935-07-24", "English"),
            new Publisher("PUB002", "HarperCollins", "1989-01-30", "English"),
            new Publisher("PUB003", "Simon & Schuster", "1924-01-02", "English"),
            new Publisher("PUB004", "Kim Đồng", "1957-06-17", "Vietnamese"),
            new Publisher("PUB005", "Nhã Nam", "2005-02-02", "Vietnamese"));

    private ObservableList<Author> authorData = FXCollections.observableArrayList(
            new Author("AUTH001", "J.K. Rowling", "Modern Fantasy"),
            new Author("AUTH002", "Stephen King", "Horror"),
            new Author("AUTH003", "Nguyễn Nhật Ánh", "Youth Literature"),
            new Author("AUTH004", "Dan Brown", "Thriller"),
            new Author("AUTH005", "Tố Hữu", "Poetry"));

    private ObservableList<Book> bookData = FXCollections.observableArrayList(
            new Book("BOOK001", "Harry Potter and the Philosopher's Stone", "First book in the Harry Potter series",
                    223, 15.99),
            new Book("BOOK002", "The Shining", "A horror novel about a family in a haunted hotel", 447, 12.99),
            new Book("BOOK003", "Mắt Biếc", "A touching Vietnamese youth novel", 300, 8.50),
            new Book("BOOK004", "The Da Vinci Code", "Thriller following symbologist Robert Langdon", 454, 14.99),
            new Book("BOOK005", "Digital Fortress", "Thriller about cryptography and NSA", 371, 11.99));

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Main title and top section
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(20));
        topSection.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Quản lý sản phẩm");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Search and buttons section
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Tìm kiếm sản phẩm...");
        searchField.setPrefWidth(300);

        Button searchButton = new Button("Tìm kiếm");
        searchButton.setStyle("-fx-background-color: #0066cc; -fx-text-fill: white;");

        Button createNewButton = new Button("Thêm sản phẩm mới");
        createNewButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        createNewButton.setOnAction(event -> showCreateNewProductWindow());

        actionBar.getChildren().addAll(searchField, searchButton, createNewButton);
        // Stats overview
        HBox statsBar = new HBox(20);
        statsBar.setPadding(new Insets(10));
        statsBar.setAlignment(Pos.CENTER);

        VBox booksStats = createStatBox("Tổng sách", "532");
        VBox authorsStats = createStatBox("Tổng tác giả", "87");
        VBox publishersStats = createStatBox("Tổng NXB", "24");
        VBox salesStats = createStatBox("Doanh thu tháng", "12.458.000đ");

        statsBar.getChildren().addAll(booksStats, authorsStats, publishersStats, salesStats);

        topSection.getChildren().addAll(titleLabel, actionBar, statsBar);
        root.setTop(topSection);

        // Publishers table
        TableView<Publisher> publishersTable = new TableView<>();
        publishersTable.setPrefHeight(150);
        publishersTable.setItems(publisherData);
        TableColumn<Publisher, String> publisherIdColumn = new TableColumn<>("Mã NXB");
        publisherIdColumn.setCellValueFactory(cellData -> cellData.getValue().publisherIdProperty());
        publisherIdColumn.setPrefWidth(100);

        TableColumn<Publisher, String> publisherNameColumn = new TableColumn<>("Tên nhà xuất bản");
        publisherNameColumn.setCellValueFactory(cellData -> cellData.getValue().publisherNameProperty());
        publisherNameColumn.setPrefWidth(200);

        TableColumn<Publisher, String> publishDateColumn = new TableColumn<>("Ngày thành lập");
        publishDateColumn.setCellValueFactory(cellData -> cellData.getValue().establishedDateProperty());
        publishDateColumn.setPrefWidth(120);

        TableColumn<Publisher, String> languageColumn = new TableColumn<>("Ngôn ngữ");
        languageColumn.setCellValueFactory(cellData -> cellData.getValue().languageProperty());
        languageColumn.setPrefWidth(100);

        TableColumn<Publisher, Void> actionColumn = new TableColumn<>("Thao tác");
        actionColumn.setPrefWidth(120);
        actionColumn.setCellFactory(param -> new TableCell<Publisher, Void>() {
            private final Button editBtn = new Button("Sửa");
            private final Button deleteBtn = new Button("Xóa");

            {
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                HBox pane = new HBox(5);
                pane.setAlignment(Pos.CENTER);
                pane.getChildren().addAll(editBtn, deleteBtn);

                editBtn.setOnAction(event -> {
                    Publisher data = getTableView().getItems().get(getIndex());
                    // Edit functionality here
                    System.out.println("Edit button clicked for: " + data.getPublisherName());
                });

                deleteBtn.setOnAction(event -> {
                    Publisher data = getTableView().getItems().get(getIndex());
                    // Delete functionality here
                    System.out.println("Delete button clicked for: " + data.getPublisherName());
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
        });

        // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        publishersTable.getColumns().add(publisherIdColumn);
        publishersTable.getColumns().add(publisherNameColumn);
        publishersTable.getColumns().add(publishDateColumn);
        publishersTable.getColumns().add(languageColumn);
        publishersTable.getColumns().add(actionColumn);

        // Authors table
        TableView<Author> authorsTable = new TableView<>();
        authorsTable.setPrefHeight(150);
        authorsTable.setItems(authorData);
        TableColumn<Author, String> authorIdColumn = new TableColumn<>("Mã tác giả");
        authorIdColumn.setCellValueFactory(cellData -> cellData.getValue().authorIdProperty());
        authorIdColumn.setPrefWidth(100);

        TableColumn<Author, String> authorNameColumn = new TableColumn<>("Tên tác giả");
        authorNameColumn.setCellValueFactory(cellData -> cellData.getValue().authorNameProperty());
        authorNameColumn.setPrefWidth(200);

        TableColumn<Author, String> genreColumn = new TableColumn<>("Thể loại");
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        genreColumn.setPrefWidth(150);

        TableColumn<Author, Void> authorActionColumn = new TableColumn<>("Thao tác");
        authorActionColumn.setPrefWidth(120);
        authorActionColumn.setCellFactory(param -> new TableCell<Author, Void>() {
            private final Button editBtn = new Button("Sửa");
            private final Button deleteBtn = new Button("Xóa");

            {
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Author data = getTableView().getItems().get(getIndex());
                    // Edit functionality here
                    System.out.println("Edit button clicked for: " + data.getAuthorName());
                });

                deleteBtn.setOnAction(event -> {
                    Author data = getTableView().getItems().get(getIndex());
                    // Delete functionality here
                    System.out.println("Delete button clicked for: " + data.getAuthorName());
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
        });

        // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        authorsTable.getColumns().add(authorIdColumn);
        authorsTable.getColumns().add(authorNameColumn);
        authorsTable.getColumns().add(genreColumn);
        authorsTable.getColumns().add(authorActionColumn);

        // Books table
        TableView<Book> booksTable = new TableView<>();
        booksTable.setPrefHeight(200);
        booksTable.setItems(bookData);
        TableColumn<Book, String> bookIdColumn = new TableColumn<>("Mã sách");
        bookIdColumn.setCellValueFactory(cellData -> cellData.getValue().bookIdProperty());
        bookIdColumn.setPrefWidth(100);

        TableColumn<Book, String> bookTitleColumn = new TableColumn<>("Tiêu đề");
        bookTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        bookTitleColumn.setPrefWidth(200);

        TableColumn<Book, String> descriptionColumn = new TableColumn<>("Mô tả");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        descriptionColumn.setPrefWidth(250);

        TableColumn<Book, Number> pageColumn = new TableColumn<>("Số trang");
        pageColumn.setCellValueFactory(cellData -> cellData.getValue().pagesProperty());
        pageColumn.setPrefWidth(70);

        TableColumn<Book, Number> priceColumn = new TableColumn<>("Giá");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        priceColumn.setPrefWidth(70);
        priceColumn.setCellFactory(column -> {
            return new TableCell<Book, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f₫", item.doubleValue() * 23000)); // Convert to VND
                    }
                }
            };
        });

        TableColumn<Book, Void> bookActionColumn = new TableColumn<>("Thao tác");
        bookActionColumn.setPrefWidth(180);
        bookActionColumn.setCellFactory(param -> new TableCell<Book, Void>() {
            private final Button editBtn = new Button("Sửa");
            private final Button deleteBtn = new Button("Xóa");
            private final Button stockBtn = new Button("Kho");

            {
                editBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                stockBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");

                editBtn.setOnAction(event -> {
                    Book data = getTableView().getItems().get(getIndex());
                    // Edit functionality here
                    System.out.println("Edit button clicked for: " + data.getTitle());
                });

                deleteBtn.setOnAction(event -> {
                    Book data = getTableView().getItems().get(getIndex());
                    // Delete functionality here
                    System.out.println("Delete button clicked for: " + data.getTitle());
                });

                stockBtn.setOnAction(event -> {
                    Book data = getTableView().getItems().get(getIndex());
                    // Stock management functionality
                    System.out.println("Stock button clicked for: " + data.getTitle());
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
                    pane.getChildren().addAll(editBtn, deleteBtn, stockBtn);
                    setGraphic(pane);
                }
            }
        });

        // Thêm từng cột riêng lẻ để tránh cảnh báo type safety
        booksTable.getColumns().add(bookIdColumn);
        booksTable.getColumns().add(bookTitleColumn);
        booksTable.getColumns().add(descriptionColumn);
        booksTable.getColumns().add(pageColumn);
        booksTable.getColumns().add(priceColumn);
        booksTable.getColumns().add(bookActionColumn);

        // Add tables to layout with TabPane for better organization
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        Tab booksTab = new Tab("Sách");
        VBox booksBox = new VBox(10);
        booksBox.setPadding(new Insets(10));

        HBox booksHeader = new HBox(10);
        Label bookLabel = new Label("Kho sách");
        bookLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button importBooksBtn = new Button("Nhập sách");
        importBooksBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        Button exportBooksBtn = new Button("Xuất báo cáo");
        exportBooksBtn.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");

        booksHeader.getChildren().addAll(bookLabel, new Pane(), importBooksBtn, exportBooksBtn);
        HBox.setHgrow(new Pane(), Priority.ALWAYS);

        booksBox.getChildren().addAll(booksHeader, booksTable);
        booksTab.setContent(booksBox);
        Tab authorsTab = new Tab("Tác giả");
        VBox authorsBox = new VBox(10);
        authorsBox.setPadding(new Insets(10));
        Label authorLabel = new Label("Danh sách tác giả");
        authorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        authorsBox.getChildren().addAll(authorLabel, authorsTable);
        authorsTab.setContent(authorsBox);

        Tab publishersTab = new Tab("Nhà xuất bản");
        VBox publishersBox = new VBox(10);
        publishersBox.setPadding(new Insets(10));
        Label publisherLabel = new Label("Danh sách nhà xuất bản");
        publisherLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        publishersBox.getChildren().addAll(publisherLabel, publishersTable);
        publishersTab.setContent(publishersBox);

        // Thêm từng tab riêng lẻ để tránh cảnh báo type safety
        tabPane.getTabs().add(booksTab);
        tabPane.getTabs().add(authorsTab);
        tabPane.getTabs().add(publishersTab);

        root.setCenter(tabPane); // Status bar at the bottom
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #f8f9fa;");
        Label statusLabel = new Label("Sẵn sàng");
        statusBar.getChildren().add(statusLabel);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setTitle("Quản lý hiệu sách - Sản phẩm");
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private void showCreateNewProductWindow() {
        Stage newWindow = new Stage();

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // Book Tab
        Tab bookTab = new Tab("Sách mới");
        VBox bookForm = new VBox(10);
        bookForm.setPadding(new Insets(20));

        Label bookTitle = new Label("Thêm sách mới");
        bookTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane bookGrid = new GridPane();
        bookGrid.setHgap(10);
        bookGrid.setVgap(10);
        bookGrid.setPadding(new Insets(10));
        bookGrid.add(new Label("Mã sách:"), 0, 0);
        TextField bookIdField = new TextField();
        bookIdField.setPromptText("Tự động tạo");
        bookIdField.setDisable(true);
        bookGrid.add(bookIdField, 1, 0);

        bookGrid.add(new Label("Tiêu đề:"), 0, 1);
        TextField titleField = new TextField();
        bookGrid.add(titleField, 1, 1);

        bookGrid.add(new Label("Mô tả:"), 0, 2);
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(3);
        bookGrid.add(descriptionArea, 1, 2);

        bookGrid.add(new Label("Nhà xuất bản:"), 0, 3);
        ComboBox<String> publisherCombo = new ComboBox<>();
        publisherCombo.getItems().addAll("Penguin Random House", "HarperCollins", "Simon & Schuster", "Kim Đồng",
                "Nhã Nam");
        bookGrid.add(publisherCombo, 1, 3);

        bookGrid.add(new Label("Tác giả:"), 0, 4);
        ComboBox<String> authorCombo = new ComboBox<>();
        authorCombo.getItems().addAll("J.K. Rowling", "Stephen King", "Nguyễn Nhật Ánh", "Dan Brown", "Tố Hữu");
        bookGrid.add(authorCombo, 1, 4);

        bookGrid.add(new Label("Số trang:"), 0, 5);
        TextField pagesField = new TextField();
        bookGrid.add(pagesField, 1, 5);

        bookGrid.add(new Label("Giá:"), 0, 6);
        TextField priceField = new TextField();
        bookGrid.add(priceField, 1, 6);

        bookGrid.add(new Label("Số lượng:"), 0, 7);
        TextField stockField = new TextField();
        bookGrid.add(stockField, 1, 7);
        HBox bookButtons = new HBox(10);
        Button saveBookButton = new Button("Lưu sách");
        saveBookButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelBookButton = new Button("Hủy");
        bookButtons.getChildren().addAll(saveBookButton, cancelBookButton);
        bookButtons.setAlignment(Pos.CENTER_RIGHT);

        bookForm.getChildren().addAll(bookTitle, bookGrid, bookButtons);
        bookTab.setContent(bookForm);
        // Author Tab
        Tab authorTab = new Tab("Tác giả mới");
        VBox authorForm = new VBox(10);
        authorForm.setPadding(new Insets(20));

        Label authorTitle = new Label("Thêm tác giả mới");
        authorTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane authorGrid = new GridPane();
        authorGrid.setHgap(10);
        authorGrid.setVgap(10);
        authorGrid.setPadding(new Insets(10));
        authorGrid.add(new Label("Mã tác giả:"), 0, 0);
        TextField authorIdField = new TextField();
        authorIdField.setPromptText("Tự động tạo");
        authorIdField.setDisable(true);
        authorGrid.add(authorIdField, 1, 0);

        authorGrid.add(new Label("Tên:"), 0, 1);
        TextField authorNameField = new TextField();
        authorGrid.add(authorNameField, 1, 1);

        authorGrid.add(new Label("Thể loại:"), 0, 2);
        TextField genreField = new TextField();
        authorGrid.add(genreField, 1, 2);

        HBox authorButtons = new HBox(10);
        Button saveAuthorButton = new Button("Lưu tác giả");
        saveAuthorButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelAuthorButton = new Button("Hủy");
        authorButtons.getChildren().addAll(saveAuthorButton, cancelAuthorButton);
        authorButtons.setAlignment(Pos.CENTER_RIGHT);

        authorForm.getChildren().addAll(authorTitle, authorGrid, authorButtons);
        authorTab.setContent(authorForm);
        // Publisher Tab
        Tab publisherTab = new Tab("Nhà xuất bản mới");
        VBox publisherForm = new VBox(10);
        publisherForm.setPadding(new Insets(20));

        Label publisherTitle = new Label("Thêm nhà xuất bản mới");
        publisherTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane publisherGrid = new GridPane();
        publisherGrid.setHgap(10);
        publisherGrid.setVgap(10);
        publisherGrid.setPadding(new Insets(10));
        publisherGrid.add(new Label("Mã NXB:"), 0, 0);
        TextField publisherIdField = new TextField();
        publisherIdField.setPromptText("Tự động tạo");
        publisherIdField.setDisable(true);
        publisherGrid.add(publisherIdField, 1, 0);

        publisherGrid.add(new Label("Tên:"), 0, 1);
        TextField publisherNameField = new TextField();
        publisherGrid.add(publisherNameField, 1, 1);

        publisherGrid.add(new Label("Ngày thành lập:"), 0, 2);
        DatePicker establishedDatePicker = new DatePicker();
        publisherGrid.add(establishedDatePicker, 1, 2);

        publisherGrid.add(new Label("Ngôn ngữ:"), 0, 3);
        TextField languageField = new TextField();
        publisherGrid.add(languageField, 1, 3);

        HBox publisherButtons = new HBox(10);
        Button savePublisherButton = new Button("Lưu nhà xuất bản");
        savePublisherButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        Button cancelPublisherButton = new Button("Hủy");
        publisherButtons.getChildren().addAll(savePublisherButton, cancelPublisherButton);
        publisherButtons.setAlignment(Pos.CENTER_RIGHT);

        publisherForm.getChildren().addAll(publisherTitle, publisherGrid, publisherButtons);
        publisherTab.setContent(publisherForm);

        // Thêm từng tab riêng lẻ để tránh cảnh báo type safety
        tabPane.getTabs().add(bookTab);
        tabPane.getTabs().add(authorTab);
        tabPane.getTabs().add(publisherTab);
        Scene newScene = new Scene(tabPane, 500, 500);
        newWindow.setTitle("Thêm sản phẩm mới");
        newWindow.setScene(newScene);
        newWindow.show();
        // Action handlers
        cancelBookButton.setOnAction(e -> {
            // Đóng cửa sổ
            newWindow.close();
        });
        cancelAuthorButton.setOnAction(e -> {
            // Đóng cửa sổ
            newWindow.close();
        });
        cancelPublisherButton.setOnAction(e -> {
            // Đóng cửa sổ
            newWindow.close();
        });

        saveBookButton.setOnAction(e -> {
            // Logic lưu sách vào cơ sở dữ liệu
            System.out.println("Đang lưu sách mới...");
            newWindow.close();
        });

        saveAuthorButton.setOnAction(e -> {
            // Logic lưu tác giả vào cơ sở dữ liệu
            System.out.println("Đang lưu tác giả mới...");
            newWindow.close();
        });

        savePublisherButton.setOnAction(e -> {
            // Logic lưu nhà xuất bản vào cơ sở dữ liệu
            System.out.println("Đang lưu nhà xuất bản mới...");
            newWindow.close();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Model classes
    public static class Publisher {
        private javafx.beans.property.SimpleStringProperty publisherId;
        private javafx.beans.property.SimpleStringProperty publisherName;
        private javafx.beans.property.SimpleStringProperty establishedDate;
        private javafx.beans.property.SimpleStringProperty language;

        public Publisher(String id, String name, String date, String language) {
            this.publisherId = new javafx.beans.property.SimpleStringProperty(id);
            this.publisherName = new javafx.beans.property.SimpleStringProperty(name);
            this.establishedDate = new javafx.beans.property.SimpleStringProperty(date);
            this.language = new javafx.beans.property.SimpleStringProperty(language);
        }

        public String getPublisherId() {
            return publisherId.get();
        }

        public javafx.beans.property.SimpleStringProperty publisherIdProperty() {
            return publisherId;
        }

        public String getPublisherName() {
            return publisherName.get();
        }

        public javafx.beans.property.SimpleStringProperty publisherNameProperty() {
            return publisherName;
        }

        public String getEstablishedDate() {
            return establishedDate.get();
        }

        public javafx.beans.property.SimpleStringProperty establishedDateProperty() {
            return establishedDate;
        }

        public String getLanguage() {
            return language.get();
        }

        public javafx.beans.property.SimpleStringProperty languageProperty() {
            return language;
        }
    }

    public static class Author {
        private javafx.beans.property.SimpleStringProperty authorId;
        private javafx.beans.property.SimpleStringProperty authorName;
        private javafx.beans.property.SimpleStringProperty genre;

        public Author(String id, String name, String genre) {
            this.authorId = new javafx.beans.property.SimpleStringProperty(id);
            this.authorName = new javafx.beans.property.SimpleStringProperty(name);
            this.genre = new javafx.beans.property.SimpleStringProperty(genre);
        }

        public String getAuthorId() {
            return authorId.get();
        }

        public javafx.beans.property.SimpleStringProperty authorIdProperty() {
            return authorId;
        }

        public String getAuthorName() {
            return authorName.get();
        }

        public javafx.beans.property.SimpleStringProperty authorNameProperty() {
            return authorName;
        }

        public String getGenre() {
            return genre.get();
        }

        public javafx.beans.property.SimpleStringProperty genreProperty() {
            return genre;
        }
    }

    public static class Book {
        private javafx.beans.property.SimpleStringProperty bookId;
        private javafx.beans.property.SimpleStringProperty title;
        private javafx.beans.property.SimpleStringProperty description;
        private javafx.beans.property.SimpleIntegerProperty pages;
        private javafx.beans.property.SimpleDoubleProperty price;

        public Book(String id, String title, String description, int pages, double price) {
            this.bookId = new javafx.beans.property.SimpleStringProperty(id);
            this.title = new javafx.beans.property.SimpleStringProperty(title);
            this.description = new javafx.beans.property.SimpleStringProperty(description);
            this.pages = new javafx.beans.property.SimpleIntegerProperty(pages);
            this.price = new javafx.beans.property.SimpleDoubleProperty(price);
        }

        public String getBookId() {
            return bookId.get();
        }

        public javafx.beans.property.SimpleStringProperty bookIdProperty() {
            return bookId;
        }

        public String getTitle() {
            return title.get();
        }

        public javafx.beans.property.SimpleStringProperty titleProperty() {
            return title;
        }

        public String getDescription() {
            return description.get();
        }

        public javafx.beans.property.SimpleStringProperty descriptionProperty() {
            return description;
        }

        public int getPages() {
            return pages.get();
        }

        public javafx.beans.property.SimpleIntegerProperty pagesProperty() {
            return pages;
        }

        public double getPrice() {
            return price.get();
        }

        public javafx.beans.property.SimpleDoubleProperty priceProperty() {
            return price;
        }
    }
}