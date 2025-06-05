package org.example;

import javax.swing.*;
import java.awt.*;

public class ReportsUI {
    public static void showReports() {
        JFrame reportsFrame = new JFrame("View Reports");
        reportsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reportsFrame.setSize(800, 600);
        reportsFrame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Reports Section", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        reportsFrame.add(label);
        reportsFrame.setVisible(true);
    }
}
