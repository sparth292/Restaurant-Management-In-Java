package com.mycompany.restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;

public class RestaurantManagement extends JFrame {

    public RestaurantManagement() {
        setTitle("Restaurant Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen

        // Create main panel with BoxLayout (Y_AXIS)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300)); // Padding
        mainPanel.setBackground(Color.LIGHT_GRAY); // Optional background color

        // Heading
        JLabel heading = new JLabel("Ashoka Restaurant");
        heading.setFont(new Font("Arial", Font.BOLD, 32));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(heading);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Buttons
        Font btnFont = new Font("Arial", Font.PLAIN, 24);
        
        JButton btnCreateBill = createStyledButton("Create a Bill", btnFont);
        JButton btnAddItem = createStyledButton("Add a New Item", btnFont);
        JButton btnDeleteItem = createStyledButton("Delete an Item", btnFont);
        JButton btnExit = createStyledButton("Exit", btnFont);

        mainPanel.add(btnCreateBill);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(btnAddItem);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(btnDeleteItem);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(btnExit);

        add(mainPanel); // Add to frame

        // Action listeners
        btnCreateBill.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Opening Bill Creation Screen...");
            new BillingSystem();
        });

        btnAddItem.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this, "Opening Add Item Screen...");
            new AddItem();
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

    // Utility method to create styled buttons
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
