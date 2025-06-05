package org.example;

import javax.swing.*;
import java.awt.*;

public class ProductsUI {
    public static void showProducts() {
        JFrame productsFrame = new JFrame("Manage Products");
        productsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productsFrame.setSize(800, 600);
        productsFrame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Products Management Section", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        productsFrame.add(label);
        productsFrame.setVisible(true);
    }
}
