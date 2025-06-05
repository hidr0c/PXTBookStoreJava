package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardUI {
    public static void showDashboard() {
        JFrame dashboardFrame = new JFrame("Dashboard");
        dashboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashboardFrame.setSize(1000, 700);
        dashboardFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Drawer for navigation
        JPanel drawerPanel = new JPanel();
        drawerPanel.setLayout(new GridLayout(5, 1, 10, 10));
        drawerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        drawerPanel.setBackground(new Color(240, 240, 240));

        JButton dashboardButton = new JButton("Dashboard");
        JButton storageButton = new JButton("Manage Storage");
        JButton productButton = new JButton("Manage Products");
        JButton staffButton = new JButton("Manage Staff");
        JButton ordersButton = new JButton("Manage Orders");

        JButton[] buttons = { dashboardButton, storageButton, productButton, staffButton, ordersButton };
        for (JButton button : buttons) {
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBackground(new Color(0, 102, 204));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            drawerPanel.add(button);
        }

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
        dashboardButton.addActionListener(e -> ReportsUI.showReports());
        storageButton.addActionListener(e -> StorageUI.showStorage());
        productButton.addActionListener(e -> ProductsUI.showProducts());
        staffButton.addActionListener(e -> StaffUI.showStaff());
        ordersButton.addActionListener(e -> OrdersUI.showOrders());
    }
}
