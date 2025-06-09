package com.mycompany.restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RestaurantManagement extends JFrame {

    public RestaurantManagement() {
        setTitle("Restaurant Billing System");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main split panel: left (label) + right (buttons)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ===== LEFT Panel =====
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setLayout(new GridBagLayout()); // Center the label

        JLabel heading = new JLabel("Ashoka Restaurant");
        heading.setFont(new Font("Arial", Font.BOLD, 40));
        heading.setForeground(Color.DARK_GRAY);
        leftPanel.add(heading); // Centered using GridBagLayout

        // ===== RIGHT Panel =====
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100)); // Padding
        rightPanel.setBackground(Color.WHITE); // Optional

        Font btnFont = new Font("Arial", Font.PLAIN, 24);

        JButton btnCreateBill = createStyledButton("Create a Bill", btnFont);
        JButton btnAddItem = createStyledButton("Add a New Item", btnFont);
        JButton btnExportExcel = createStyledButton("Export To Excel", btnFont);
        JButton btnDeleteItem = createStyledButton("Delete an Item", btnFont);
        JButton btnExit = createStyledButton("Exit", btnFont);

        rightPanel.add(Box.createRigidArea(new Dimension(0, 70)));
        rightPanel.add(btnCreateBill);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(btnAddItem);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(btnExportExcel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(btnDeleteItem);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        rightPanel.add(btnExit);

        // Add both panels to main
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);

        // Action listeners (same logic)
        btnCreateBill.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Opening Bill Creation Screen...");
            new BillingSystem();
        });

        btnAddItem.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Opening Add Item Screen...");
            new AddItem();
        });

        btnExportExcel.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Opening Export To Excel Screen...");
            new ExportScreen();
        });

        btnDeleteItem.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Opening Delete Item Screen...");
            new DeleteItem();
        });

        btnExit.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?");
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private JButton createStyledButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RestaurantManagement screen = new RestaurantManagement();
            screen.setVisible(true);
        });
    }
}
