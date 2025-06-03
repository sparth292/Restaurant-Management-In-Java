    package com.mycompany.restaurantmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class DeleteItem extends JFrame {

    JTable table;
    DefaultTableModel model;
    JButton deleteButton;

    public DeleteItem() {
        setTitle("Delete Item");
        setSize(600, 400);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 20, 540, 250);
        add(scrollPane);

        // Delete Button
        deleteButton = new JButton("Delete Selected Item");
        deleteButton.setBounds(200, 290, 180, 30);
        add(deleteButton);

        // Load data from DB
        loadTableData();

        // Delete button listener
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedItem();
            }
        });

        setVisible(true);
    }

    private void loadTableData() {
        model.setRowCount(0); // Clear existing data
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:restaurant.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM items")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                model.addRow(new Object[]{id, name, price});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item ID " + id + "?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:restaurant.db");
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM items WHERE id = ?")) {

                    pstmt.setInt(1, id);
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Item deleted.");
                        loadTableData(); // Refresh table
                    } else {
                        JOptionPane.showMessageDialog(this, "Item not found or already deleted.");
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }
}
