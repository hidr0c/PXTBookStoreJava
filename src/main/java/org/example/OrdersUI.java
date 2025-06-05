package org.example;

import javax.swing.*;
import java.awt.*;

public class OrdersUI {
    public static void showOrders() {
        JFrame ordersFrame = new JFrame("Manage Orders");
        ordersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ordersFrame.setSize(800, 600);
        ordersFrame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Orders Management Section", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        ordersFrame.add(label);
        ordersFrame.setVisible(true);
    }
}
