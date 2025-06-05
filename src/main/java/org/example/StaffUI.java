package org.example;

import javax.swing.*;
import java.awt.*;

public class StaffUI {
    public static void showStaff() {
        JFrame staffFrame = new JFrame("Manage Staff");
        staffFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        staffFrame.setSize(800, 600);
        staffFrame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Staff Management Section", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        staffFrame.add(label);
        staffFrame.setVisible(true);
    }
}
