package com.mycompany.restaurantmanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class BillingSystem extends JFrame {

    private JTable menuTable;
    private JTextField searchField;
    private DefaultTableModel model;

    public BillingSystem() {
        setTitle("Billing System");
        setSize(600, 500);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> loadMenuItems(searchField.getText()));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Select", "ID", "Name", "Price"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        menuTable = new JTable(model);
        add(new JScrollPane(menuTable), BorderLayout.CENTER);

        JButton createBillBtn = new JButton("Create Bill");
        createBillBtn.addActionListener(e -> showConfirmationDialog());
        add(createBillBtn, BorderLayout.SOUTH);

        loadMenuItems("");
        setVisible(true);
    }

    private void loadMenuItems(String keyword) {
        model.setRowCount(0);
        String sql = "SELECT id, name, price FROM items WHERE name LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(false);
                row.add(rs.getInt("id"));
                row.add(rs.getString("name"));
                row.add(rs.getDouble("price"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading menu: " + e.getMessage());
        }
    }

    private void showConfirmationDialog() {
        boolean hasSelection = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                hasSelection = true;
                break;
            }
        }

        if (!hasSelection) {
            JOptionPane.showMessageDialog(this, "Please select at least one item using the checkbox.");
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Is the order correct? Do you still want to make changes?",
                "Confirm Billing",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.NO_OPTION) {
            showBillWindow();
        }
    }

    private void showBillWindow() {
        JFrame billFrame = new JFrame("Bill");
        billFrame.setSize(400, 400);
        billFrame.setLayout(new BorderLayout());

        DefaultTableModel billModel = new DefaultTableModel(new String[]{"Name", "Price"}, 0);
        JTable billTable = new JTable(billModel);

        double total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                String name = model.getValueAt(i, 2).toString();
                double price = (double) model.getValueAt(i, 3);
                billModel.addRow(new Object[]{name, price});
                total += price;
            }
        }

        JLabel totalLabel = new JLabel(String.format("Total: ₹%.2f", total));
        JButton proceedBtn = new JButton("Proceed with Bill");

        double finalTotal = total;
        proceedBtn.addActionListener(e -> saveBillToDB(billModel, finalTotal));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(totalLabel);
        bottomPanel.add(proceedBtn);

        billFrame.add(new JScrollPane(billTable), BorderLayout.CENTER);
        billFrame.add(bottomPanel, BorderLayout.SOUTH);

        billFrame.setVisible(true);
    }

    private void saveBillToDB(DefaultTableModel billModel, double subtotal) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "SQLite JDBC Driver not found.");
            return;
        }

        double cgst = subtotal * 0.025;
        double sgst = subtotal * 0.025;
        double totalAmount = subtotal + cgst + sgst;

        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS bills (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                items TEXT,
                subtotal REAL,
                cgst REAL,
                sgst REAL,
                total REAL,
                timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
            );
        """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:bills.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);

            StringBuilder itemList = new StringBuilder();
            for (int i = 0; i < billModel.getRowCount(); i++) {
                itemList.append(billModel.getValueAt(i, 0)).append(", ");
            }
            if (itemList.length() > 0) itemList.setLength(itemList.length() - 2);

            PreparedStatement insertStmt = conn.prepareStatement("""
                INSERT INTO bills(items, subtotal, cgst, sgst, total)
                VALUES (?, ?, ?, ?, ?)
            """);

            insertStmt.setString(1, itemList.toString());
            insertStmt.setDouble(2, subtotal);
            insertStmt.setDouble(3, cgst);
            insertStmt.setDouble(4, sgst);
            insertStmt.setDouble(5, totalAmount);
            insertStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, String.format(
                    "Bill saved!\nSubtotal: ₹%.2f\nCGST: ₹%.2f\nSGST: ₹%.2f\nTotal: ₹%.2f",
                    subtotal, cgst, sgst, totalAmount));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving bill: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingSystem::new);
    }
}
