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

@SuppressWarnings("unused")
public class DashboardStats {
    // Model đơn giản cho hóa đơn, khách hàng, thể loại
    private static class Invoice {
        String invoiceNumber, salesperson, status, customer, date;
        double amount;

        public Invoice(String i, String s, String st, String c, String d, double a) {
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

    private static class CustomerStat {
        String customer;
        double total;

        public CustomerStat(String c, double t) {
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

    private static class CategoryStat {
        String category;
        double total;

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
                LocalDate localDate = LocalDate.parse(date, dtf);
                String month = localDate.getMonth() + " " + localDate.getYear();
                monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + amount);
                customerTotals.put(customer, customerTotals.getOrDefault(customer, 0.0) + amount);
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
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> revenueChart = new LineChart<>(new javafx.scene.chart.CategoryAxis(), yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (String month : monthlyRevenue.keySet()) {
            series.getData().add(new XYChart.Data<>(month, monthlyRevenue.get(month)));
        }
        revenueChart.getData().add(series);
        TableView<CustomerStat> customerTable = new TableView<>();
        ObservableList<CustomerStat> customerStats = FXCollections.observableArrayList();
        customerTotals.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(e -> customerStats.add(new CustomerStat(e.getKey(), e.getValue())));
        TableColumn<CustomerStat, String> colCustNo = new TableColumn<>("STT");
        colCustNo.setCellFactory(col -> new TableCell<CustomerStat, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colCustNo.setPrefWidth(50);
        TableColumn<CustomerStat, String> colCustName = new TableColumn<>("Khách hàng");
        colCustName.setCellValueFactory(new PropertyValueFactory<>("customer"));
        colCustName.setPrefWidth(500);
        TableColumn<CustomerStat, Double> colCustTotal = new TableColumn<>("Số lượng");
        colCustTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCustTotal.setPrefWidth(200);
        customerTable.setItems(customerStats);
        customerTable.getColumns().addAll(colCustNo, colCustName, colCustTotal);
        TableView<CategoryStat> categoryTable = new TableView<>();
        ObservableList<CategoryStat> categoryStats = FXCollections.observableArrayList();
        categoryTotals.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(e -> categoryStats.add(new CategoryStat(e.getKey(), e.getValue())));
        TableColumn<CategoryStat, String> colCatNo = new TableColumn<>("STT");
        colCatNo.setCellFactory(col -> new TableCell<CategoryStat, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        colCatNo.setPrefWidth(50);
        TableColumn<CategoryStat, String> colCatName = new TableColumn<>("Thể loại");
        colCatName.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCatName.setPrefWidth(500);
        TableColumn<CategoryStat, Double> colCatTotal = new TableColumn<>("Số lượng");
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