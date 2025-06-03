package com.mycompany.restaurantmanagement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.*;
import javax.swing.*;

public class AddItem extends JFrame {

    // Fonts
    Font labelFont = new Font("Arial", Font.PLAIN, 24);
    Font btnFont = new Font("Arial", Font.PLAIN, 24);

    // Labels
    JLabel label = createStyledLabel("Add Item", labelFont);
    JLabel id = createStyledLabel("Item Id : ", labelFont);
    JLabel name = createStyledLabel("Item Name : ", labelFont);
    JLabel price = createStyledLabel("Item Price : ", labelFont);

    // TextFields
    JTextField idTextField = styledTextField();
    JTextField nameTextField = styledTextField();
    JTextField priceTextField = styledTextField();

    // Panel
    JPanel billingPanel = new JPanel();

    // Button
    JButton addItemButton = createdStyledButton("Add Item", btnFont);

    public AddItem() {
        // Set layout and background
        billingPanel.setLayout(new BoxLayout(billingPanel, BoxLayout.Y_AXIS));
        billingPanel.setBorder(BorderFactory.createEmptyBorder(100, 300, 100, 300)); // Padding
        billingPanel.setBackground(Color.LIGHT_GRAY);

        // Heading
        label.setAlignmentX(CENTER_ALIGNMENT);
        billingPanel.add(label);
        billingPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        // Add Label-Field rows
        billingPanel.add(createFieldRow(id, idTextField));
        billingPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        billingPanel.add(createFieldRow(name, nameTextField));
        billingPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        billingPanel.add(createFieldRow(price, priceTextField));
        billingPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        billingPanel.add(addItemButton);

        // Button Action: Save item to SQLite DB
        addItemButton.addActionListener((ActionEvent e) -> {
            String itemId = idTextField.getText();
            String itemName = nameTextField.getText();
            String itemPriceStr = priceTextField.getText();

            if (itemId.isEmpty() || itemName.isEmpty() || itemPriceStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                double itemPrice = Double.parseDouble(itemPriceStr);

                Connection conn = DatabaseUtil.getConnection();

                Statement stmt = conn.createStatement();

                // Create table if it doesn't exist
                stmt.execute("CREATE TABLE IF NOT EXISTS items (id TEXT PRIMARY KEY, name TEXT, price REAL)");

                // Insert item
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO items(id, name, price) VALUES (?, ?, ?)");
                pstmt.setString(1, itemId);
                pstmt.setString(2, itemName);
                pstmt.setDouble(3, itemPrice);

                pstmt.executeUpdate();
                conn.close();

                JOptionPane.showMessageDialog(this, "Item Added Successfully!");

                // Clear fields
                idTextField.setText("");
                nameTextField.setText("");
                priceTextField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price must be a valid number.");
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Item ID already exists.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Frame setup
        setTitle("Billing Section");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        add(billingPanel);
        setVisible(true);
    }

    // Utility: Create styled label
    private static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    // Utility: Create styled text field
    private static JTextField styledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    // Utility: Combine label and field in a row
    private static JPanel createFieldRow(JLabel label, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(Color.LIGHT_GRAY);

        label.setPreferredSize(new Dimension(150, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        row.add(label);
        row.add(Box.createRigidArea(new Dimension(20, 0)));
        row.add(field);

        return row;
    }

    // Utility: Create styled button
    private JButton createdStyledButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 50));
        return button;
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddItem());
    }
}
