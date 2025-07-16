package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

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

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public String getSalesperson() {
            return salesperson;
        }

        public String getStatus() {
            return status;
        }

        public String getCustomer() {
            return customer;
        }

        public String getDate() {
            return date;
        }

        public double getAmount() {
            return amount;
        }
    }

    @SuppressWarnings("unused") // Suppress warnings for unused getters required by JavaFX properties
    public static class CustomerStat {
        private String customerId;
        private String rank;
        private double spending;

        public CustomerStat(String customerId, String rank, double spending) {
            this.customerId = customerId;
            this.rank = rank;
            this.spending = spending;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getRank() {
            return rank;
        }

        public double getSpending() {
            return spending;
        }
    }

    @SuppressWarnings("unused") // Suppress warnings for unused getters required by JavaFX properties
    public static class CategoryStat {
        private String category;
        private double total;

        public CategoryStat(String c, double t) {
            category = c;
            total = t;
        }

        public String getCategory() {
            return category;
        }

        public double getTotal() {
            return total;
        }
    }

    public static VBox createDashboardContent() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> ordersCol = db.getCollection("Orders");
        MongoCollection<Document> customersCol = db.getCollection("Customers");

        // Revenue by month
        Map<String, Double> monthlyRevenue = new java.util.LinkedHashMap<>();
        // Revenue by category (nameCategory)
        Map<String, Double> categoryTotals = new HashMap<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // Use LinkedHashMap to preserve insertion order for months
        Map<String, Double> tempMonthlyRevenue = new HashMap<>();
        Map<String, LocalDate> monthToDate = new HashMap<>();
        // Restore: Read Orders for monthly revenue
        try (MongoCursor<Document> cursor = ordersCol.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String orderDate = doc.getString("orderDate");
                Object totalObj = doc.get("total");
                double total = 0.0;
                if (totalObj instanceof Number) {
                    total = ((Number) totalObj).doubleValue();
                }
                if (orderDate != null) {
                    LocalDate localDate = LocalDate.parse(orderDate, dtf);
                    String month = String.format("%04d-%02d", localDate.getYear(), localDate.getMonthValue());
                    tempMonthlyRevenue.put(month, tempMonthlyRevenue.getOrDefault(month, 0.0) + total);
                    monthToDate.put(month, localDate.withDayOfMonth(1));
                }
            }
        }
        // For category stats: read directly from OrderDetails
        MongoCollection<Document> booksCol = db.getCollection("Books");
        MongoCollection<Document> categoriesCol = db.getCollection("Categories");
        MongoCollection<Document> orderDetailsCol = db.getCollection("OrderDetails");
        Map<String, String> bookIdToCategoryId = new HashMap<>();
        Map<String, String> categoryIdToName = new HashMap<>();
        // Build bookId -> categoryId map
        try (MongoCursor<Document> cursor = booksCol.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String bookId = doc.getString("bookId");
                String categoryId = doc.getString("categoryId");
                if (bookId != null && categoryId != null) {
                    bookIdToCategoryId.put(bookId, categoryId);
                }
            }
        }
        // Build categoryId -> nameCategory map
        try (MongoCursor<Document> cursor = categoriesCol.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String categoryId = doc.getString("categoryId");
                String nameCategory = doc.getString("nameCategory");
                if (categoryId != null && nameCategory != null) {
                    categoryIdToName.put(categoryId, nameCategory);
                }
            }
        }
        // Process orderDetails
        try (MongoCursor<Document> cursor = orderDetailsCol.find().iterator()) {
            while (cursor.hasNext()) {
                Document od = cursor.next();
                String bookId = od.getString("bookId");
                int quantity = 0;
                double unitPrice = 0.0;
                Object qObj = od.get("quantity");
                if (qObj instanceof Number) quantity = ((Number) qObj).intValue();
                else if (qObj instanceof String) {
                    try { quantity = Integer.parseInt((String) qObj); } catch (Exception e) { }
                }
                Object upObj = od.get("unitPrice");
                if (upObj instanceof Number) unitPrice = ((Number) upObj).doubleValue();
                else if (upObj instanceof String) {
                    try { unitPrice = Double.parseDouble((String) upObj); } catch (Exception e) { }
                }
                double lineTotal = quantity * unitPrice;
                // Get categoryId from bookId
                String categoryId = bookIdToCategoryId.get(bookId);
                String nameCategory = categoryIdToName.get(categoryId);
                if (nameCategory != null) {
                    categoryTotals.put(nameCategory, categoryTotals.getOrDefault(nameCategory, 0.0) + lineTotal);
                }
            }
        }
        // Sort months chronologically
        List<String> sortedMonths = new ArrayList<>(tempMonthlyRevenue.keySet());
        sortedMonths.sort((a, b) -> monthToDate.get(a).compareTo(monthToDate.get(b)));
        for (String month : sortedMonths) {
            // Display as "MMM yyyy" (e.g., "Jul 2025")
            java.time.Month m = monthToDate.get(month).getMonth();
            String displayMonth = m.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH) + " " + monthToDate.get(month).getYear();
            monthlyRevenue.put(displayMonth, tempMonthlyRevenue.get(month));
        }

        // Top 5 customers by spending
        List<CustomerStat> customerStatList = new ArrayList<>();
        try (MongoCursor<Document> cursor = customersCol.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String customerId = doc.getString("customerId");
                if (customerId == null) {
                    System.out.println("[DashboardStats] Warning: customerId is null in Customers document: " + doc.toJson());
                }
                String rank = doc.containsKey("rank") ? doc.getString("rank") : "";
                double spending = 0.0;
                if (doc.containsKey("spending")) {
                    Object spendingObj = doc.get("spending");
                    if (spendingObj instanceof Number) {
                        spending = ((Number) spendingObj).doubleValue();
                    } else if (spendingObj instanceof String) {
                        try {
                            spending = Double.parseDouble((String) spendingObj);
                        } catch (NumberFormatException e) {
                            System.out.println("[DashboardStats] Warning: spending is not a number: " + spendingObj);
                        }
                    }
                }
                customerStatList.add(new CustomerStat(customerId, rank, spending));
            }
        }
        customerStatList.sort((a, b) -> Double.compare(b.spending, a.spending));
        ObservableList<CustomerStat> customerStats = FXCollections.observableArrayList(customerStatList.stream().limit(5).toList());

        // UI Labels
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
        // Use monthlyRevenue.keySet() which is now in chronological order
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

        // Top Categories Table (sorted ascending by revenue)
        TableView<CategoryStat> categoryTable = new TableView<>();
        ObservableList<CategoryStat> categoryStats = FXCollections.observableArrayList(
                categoryTotals.entrySet().stream()
                        .sorted((a, b) -> Double.compare(a.getValue(), b.getValue())) // ascending
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

        VBox vbox = new VBox(20,
                revenueLabel, revenueChart,
                cusLabel, customerTable,
                cateLabel, categoryTable);
        vbox.setPadding(new Insets(10));
        return vbox;
    }
}