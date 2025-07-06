package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
    private static class CustomerStat {
        String customer;
        double total;

        CustomerStat(String c, double t) {
            customer = c;
            total = t;
        }

        public String getCustomer() {
            return customer;
        }

        public double getTotal() {
            return total;
        }
    }

    @SuppressWarnings("unused") // Suppress warnings for unused getters required by JavaFX properties
    private static class CategoryStat {
        String category;
        double total;

        CategoryStat(String c, double t) {
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
        // Fetch data from MongoDB
        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> sales = db.getCollection("sales");

        List<Invoice> invoices = new ArrayList<>();
        Map<String, Double> monthlyRevenue = new TreeMap<>();
        Map<String, Double> customerTotals = new HashMap<>();
        Map<String, Double> categoryTotals = new HashMap<>();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (MongoCursor<Document> cursor = sales.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String invoiceNumber = doc.getString("invoiceNumber");
                String salesperson = doc.getString("salesperson");
                String status = doc.getString("status");
                String customer = doc.getString("customer");
                String date = doc.getString("date");
                double amount = doc.getDouble("amount");
                String category = doc.getString("category");

                invoices.add(new Invoice(invoiceNumber, salesperson, status, customer, date, amount));

                // Monthly revenue
                LocalDate localDate = LocalDate.parse(date, dtf);
                String month = localDate.getMonth() + " " + localDate.getYear();
                monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + amount);

                // Customer totals
                customerTotals.put(customer, customerTotals.getOrDefault(customer, 0.0) + amount);

                // Category totals
                if (category != null)
                    categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
            }
        }
        Label revenueLabel = new Label("Doanh thu theo tháng");
        revenueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label cusLabel = new Label("Khách hàng hàng đầu");
        cusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label cateLabel = new Label("Thể loại yêu thích");
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
        ObservableList<CustomerStat> customerStats = FXCollections.observableArrayList(
                customerTotals.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(5)
                        .map(e -> new CustomerStat(e.getKey(), e.getValue()))
                        .toList());
        TableColumn<CustomerStat, Void> colCustNo = new TableColumn<>("STT");
        colCustNo.setCellFactory(col -> {
            // Lambda parameter not used, but required by interface
            return new TableCell<CustomerStat, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getIndex() + 1));
                }
            };
        });
        colCustNo.setPrefWidth(50); // Small width for numbering

        TableColumn<CustomerStat, String> colCustName = new TableColumn<>("Khách hàng");
        colCustName.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colCustName.setPrefWidth(500);

        TableColumn<CustomerStat, Double> colCustTotal = new TableColumn<>("Số lượng");
        colCustTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCustTotal.setPrefWidth(200);

        customerTable.setItems(customerStats);
        customerTable.getColumns().add(colCustNo);
        customerTable.getColumns().add(colCustName);
        customerTable.getColumns().add(colCustTotal);

        // Top Categories Table
        TableView<CategoryStat> categoryTable = new TableView<>();
        ObservableList<CategoryStat> categoryStats = FXCollections.observableArrayList(
                categoryTotals.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .limit(5)
                        .map(e -> new CategoryStat(e.getKey(), e.getValue()))
                        .toList());
        TableColumn<CategoryStat, Void> colCatNo = new TableColumn<>("STT");
        colCatNo.setCellFactory(col -> {
            // Lambda parameter not used, but required by interface
            return new TableCell<CategoryStat, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : String.valueOf(getIndex() + 1));
                }
            };
        });
        colCatNo.setPrefWidth(50);

        TableColumn<CategoryStat, String> colCatName = new TableColumn<>("Thể loại");
        colCatName.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCatName.setPrefWidth(500);

        TableColumn<CategoryStat, Double> colCatTotal = new TableColumn<>("Số lượng");
        colCatTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCatTotal.setPrefWidth(200);

        categoryTable.setItems(categoryStats);
        categoryTable.getColumns().add(colCatNo);
        categoryTable.getColumns().add(colCatName);
        categoryTable.getColumns().add(colCatTotal);

        // Layout
        VBox vbox = new VBox(20,
                revenueLabel, revenueChart,
                cusLabel, customerTable,
                cateLabel, categoryTable);
        vbox.setPadding(new Insets(10));
        return vbox;
    }
}