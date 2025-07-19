package org.example;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/BookStoreDB";
            String user = "root";
            String pass = "190399";
            conn = DriverManager.getConnection(url, user, pass);
        } catch (Exception ex) {
            System.out.println("Kết nối thất bại: " + ex.getMessage());
        }
        return conn;
    }
} 