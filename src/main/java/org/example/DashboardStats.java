package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

@SuppressWarnings("unused") // Suppress warnings for unused getters and lambda parameters
public class DashboardStats {
    @SuppressWarnings("unused") // Suppress warnings for unused getters required by JavaFX properties
    private static class Invoice {
        String invoiceNumber, salesperson, status, customer, date;
        double amount;

        Invoice(String i, String s, String st, String c, String d, double a) {
            invoiceNumber = i;
            salesperson = s;
            status = st;
            customer = c;
            date = d;
            amount = a;
        }

        public String getInvoiceNumber() { return invoiceNumber; }
        public String getSalesperson() { return salesperson; }
        public String getStatus() { return status; }
        public String getCustomer() { return customer; }
        public String getDate() { return date; }
        public double getAmount() { return amount; }
    }

    @SuppressWarnings("unused") // Suppress warnings for unused getters required by JavaFX properties
    public static class CustomerStat {
        private String customerId;
        private String rankC; // Updated to rankC
        private double spending;

        public CustomerStat(String customerId, String rankC, double spending) {
            this.customerId = customerId;
            this.rankC = rankC;
            this.spending = spending;
        }

        public String getCustomerId() { return customerId; }
        public String getRank() { return rankC; } // Getter still named getRank for JavaFX
        public double getSpending() { return spending; }
    }

    @SuppressWarnings("unused") // Suppress warnings for unused getters required by JavaFX properties
    public static class CategoryStat {
        private String category;
        private double total;

        public CategoryStat(String c, double t) {
            category = c;
            total = t;
        }

        public String getCategory() { return category; }
        public double getTotal() { return total; }
    }

    public static class MySQLConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/BookStoreDB";
        private static final String USER = "your_username";
        private static final String PASSWORD = "your_password";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    public static VBox createDashboardContent() {
        // Revenue by month
        Map<String, Double> monthlyRevenue = new java.util.LinkedHashMap<>();
        // Revenue by category
        Map<String, Double> categoryTotals = new HashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, LocalDate> monthToDate = new HashMap<>();

        try (Connection conn = MySQLConnection.getConnection()) {
            // Monthly Revenue
            String orderQuery = "SELECT DATE(orderDate) AS orderDate, total FROM Orders WHERE orderDate IS NOT NULL";
            try (PreparedStatement stmt = conn.prepareStatement(orderQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String orderDate = rs.getString("orderDate");
                    double total = rs.getDouble("total");
                    if (orderDate != null) {
                        LocalDate localDate = LocalDate.parse(orderDate, dtf);
                        String month = String.format("%04d-%02d", localDate.getYear(), localDate.getMonthValue());
                        monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + total);
                        monthToDate.put(month, localDate.withDayOfMonth(1));
                    }
                }
            }

            // Category Totals (Optimized Query)
            String categoryQuery = """
                SELECT c.nameCategory, SUM(od.quantity * od.unitPrice) as total
                FROM OrderDetails od
                JOIN Books b ON od.bookID = b.bookID
                JOIN Categories c ON b.categoryID = c.categoryID
                GROUP BY c.nameCategory
                """;
            try (PreparedStatement stmt = conn.prepareStatement(categoryQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String nameCategory = rs.getString("nameCategory");
                    double total = rs.getDouble("total");
                    if (nameCategory != null) {
                        categoryTotals.put(nameCategory, total);
                    }
                }
            }

            // Top 5 Customers
            List<CustomerStat> customerStatList = new ArrayList<>();
            String customerQuery = "SELECT customerId, rankC AS rank, spending FROM Customers WHERE customerId IS NOT NULL";
            try (PreparedStatement stmt = conn.prepareStatement(customerQuery);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String customerId = rs.getString("customerId");
                    String rankC = rs.getString("rank") != null ? rs.getString("rank") : "";
                    double spending = rs.getDouble("spending");
                    customerStatList.add(new CustomerStat(customerId, rankC, spending));
                }
            }
            customerStatList.sort((a, b) -> Double.compare(b.spending, a.spending));
            ObservableList<CustomerStat> customerStats = FXCollections.observableArrayList(customerStatList.stream().limit(5).toList());

            // Sort months chronologically
            List<String> sortedMonths = new ArrayList<>(monthlyRevenue.keySet());
            sortedMonths.sort((a, b) -> monthToDate.get(a).compareTo(monthToDate.get(b)));
            Map<String, Double> displayMonthlyRevenue = new java.util.LinkedHashMap<>();
            for (String month : sortedMonths) {
                java.time.Month m = monthToDate.get(month).getMonth();
                String displayMonth = m.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH) + " " + monthToDate.get(month).getYear();
                displayMonthlyRevenue.put(displayMonth, monthlyRevenue.get(month));
            }
            monthlyRevenue = displayMonthlyRevenue;

            // UI Components (unchanged)
            Label revenueLabel = new Label("Doanh thu theo tháng");
            revenueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            Label cusLabel = new Label("Top 5 khách hàng chi tiêu cao nhất");
            cusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            Label cateLabel = new Label("Doanh thu theo thể loại");
            cateLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Monthly Revenue Chart
            NumberAxis yAxis = new NumberAxis();
            LineChart<String, Number> revenueChart = new LineChart<>(new javafx.scene.chart.CategoryAxis(), yAxis);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (String month : monthlyRevenue.keySet()) {
                series.getData().add(new XYChart.Data<>(month, monthlyRevenue.get(month)));
            }
            revenueChart.getData().add(series);

            // Top Customers Table
            TableView<CustomerStat> customerTable = new TableView<>();
            customerTable.setFixedCellSize(30);
            customerTable.setPrefHeight(30 * 6); // 5 rows + header
            TableColumn<CustomerStat, Void> colCustNo = new TableColumn<>("STT");
            colCustNo.setCellFactory(col -> new TableCell<CustomerStat, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getIndex() + 1));
                }
            });
            colCustNo.setPrefWidth(50);

            TableColumn<CustomerStat, String> colCustId = new TableColumn<>("Customer ID");
            colCustId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            colCustId.setPrefWidth(200);

            TableColumn<CustomerStat, String> colRank = new TableColumn<>("Rank");
            colRank.setCellValueFactory(new PropertyValueFactory<>("rank"));
            colRank.setPrefWidth(150);

            TableColumn<CustomerStat, Double> colSpending = new TableColumn<>("Spending");
            colSpending.setCellValueFactory(new PropertyValueFactory<>("spending"));
            colSpending.setPrefWidth(200);

            customerTable.setItems(customerStats);
            customerTable.getColumns().addAll(colCustNo, colCustId, colRank, colSpending);

            // Top Categories Table
            TableView<CategoryStat> categoryTable = new TableView<>();
            ObservableList<CategoryStat> categoryStats = FXCollections.observableArrayList(
                    categoryTotals.entrySet().stream()
                            .sorted((a, b) -> Double.compare(a.getValue(), b.getValue()))
                            .map(e -> new CategoryStat(e.getKey(), e.getValue()))
                            .toList());
            TableColumn<CategoryStat, Void> colCatNo = new TableColumn<>("STT");
            colCatNo.setCellFactory(col -> new TableCell<CategoryStat, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getIndex() + 1));
                }
            });
            colCatNo.setPrefWidth(50);

            TableColumn<CategoryStat, String> colCatName = new TableColumn<>("Thể loại");
            colCatName.setCellValueFactory(new PropertyValueFactory<>("category"));
            colCatName.setPrefWidth(300);

            TableColumn<CategoryStat, Double> colCatTotal = new TableColumn<>("Doanh thu");
            colCatTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
            colCatTotal.setPrefWidth(200);

            categoryTable.setItems(categoryStats);
            categoryTable.getColumns().addAll(colCatNo, colCatName, colCatTotal);

            VBox vbox = new VBox(20, revenueLabel, revenueChart, cusLabel, customerTable, cateLabel, categoryTable);
            vbox.setPadding(new Insets(10));
            return vbox;

        } catch (SQLException e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error connecting to database: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            return new VBox(10, errorLabel);
        }
    }
}