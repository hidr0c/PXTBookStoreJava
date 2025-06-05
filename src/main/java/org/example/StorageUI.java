package org.example;

import javax.swing.*;
import java.awt.*;

public class StorageUI {
    public static void showStorage() {
        JFrame storageFrame = new JFrame("Manage Storage");
        storageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        storageFrame.setSize(800, 600);
        storageFrame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Storage Management Section", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        storageFrame.add(label);
        storageFrame.setVisible(true);
    }
}
