package com.mycompany.restaurantmanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class ExportScreen extends JFrame {

    public ExportScreen() {
        setTitle("Export to Excel");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JButton exportDishesBtn = new JButton("Export Dishes to Excel");
        JButton exportBillsBtn = new JButton("Export Bills to Excel");

        exportDishesBtn.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        exportBillsBtn.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));

        exportDishesBtn.addActionListener((ActionEvent e) -> {
            exportToExcel("restaurant.db", "items", "dishes_output.xlsx");
        });

        exportBillsBtn.addActionListener((ActionEvent e) -> {
            exportToExcel("bills.db", "Bills", "bills_output.xlsx");
        });

        setLayout(new GridLayout(2, 1, 10, 10));
        add(exportDishesBtn);
        add(exportBillsBtn);

        setVisible(true);
    }

    private void exportToExcel(String dbPath, String tableName, String excelFileName) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
             XSSFWorkbook workbook = new XSSFWorkbook()) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            Sheet sheet = workbook.createSheet(tableName);
            Row header = sheet.createRow(0);

            for (int i = 1; i <= columnCount; i++) {
                header.createCell(i - 1).setCellValue(rsmd.getColumnName(i));
            }

            int rowIndex = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIndex++);
                for (int i = 1; i <= columnCount; i++) {
                    row.createCell(i - 1).setCellValue(rs.getString(i));
                }
            }

            try (FileOutputStream out = new FileOutputStream(excelFileName)) {
                workbook.write(out);
                JOptionPane.showMessageDialog(this, "Exported to " + excelFileName);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to export: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExportScreen());
    }
}

