package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FoodStoreUI {
    public static void main(String[] args) {
        // Create the main frame for login
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BorderLayout());
        loginPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Welcome to Food Store Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loginPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();

        formPanel.add(userLabel);
        formPanel.add(userField);
        formPanel.add(passLabel);
        formPanel.add(passField);

        loginPanel.add(formPanel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginPanel.add(loginButton, BorderLayout.SOUTH);

        loginFrame.add(loginPanel);
        loginFrame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if ("admin".equals(username) && "admin".equals(password)) {
                    loginFrame.dispose();
                    showDashboard();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static void showDashboard() {
        JFrame dashboardFrame = new JFrame("Dashboard");
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setSize(1000, 700);
        dashboardFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Drawer for navigation
        JPanel drawerPanel = new JPanel();
        drawerPanel.setLayout(new GridLayout(5, 1, 10, 10));
        drawerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton storageButton = new JButton("Manage Storage");
        JButton productButton = new JButton("Manage Products");
        JButton staffButton = new JButton("Manage Staff");
        JButton ordersButton = new JButton("Manage Orders");
        JButton reportButton = new JButton("View Reports");

        storageButton.setFont(new Font("Arial", Font.PLAIN, 14));
        productButton.setFont(new Font("Arial", Font.PLAIN, 14));
        staffButton.setFont(new Font("Arial", Font.PLAIN, 14));
        ordersButton.setFont(new Font("Arial", Font.PLAIN, 14));
        reportButton.setFont(new Font("Arial", Font.PLAIN, 14));

        drawerPanel.add(storageButton);
        drawerPanel.add(productButton);
        drawerPanel.add(staffButton);
        drawerPanel.add(ordersButton);
        drawerPanel.add(reportButton);

        mainPanel.add(drawerPanel, BorderLayout.WEST);

        // Placeholder for main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());
        JLabel placeholderLabel = new JLabel("Select an option from the drawer", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        contentPanel.add(placeholderLabel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        dashboardFrame.add(mainPanel);
        dashboardFrame.setVisible(true);

        // Add action listeners for navigation buttons
        storageButton.addActionListener(e -> placeholderLabel.setText("Manage Storage Section"));
        productButton.addActionListener(e -> placeholderLabel.setText("Manage Products Section"));
        staffButton.addActionListener(e -> placeholderLabel.setText("Manage Staff Section"));
        ordersButton.addActionListener(e -> placeholderLabel.setText("Manage Orders Section"));
        reportButton.addActionListener(e -> placeholderLabel.setText("View Reports Section"));
    }
}
