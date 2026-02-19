package org.example;

import org.example.config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Products extends JFrame {
    JTable table;
    DefaultTableModel model;

    public Products() {
        setTitle("Products");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Product ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("unit Price");
        model.addColumn("Current Stock");
        model.addColumn("Reorder Level");
        model.addColumn("Supplier ID");

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        loadProducts();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        add(backButton,BorderLayout.SOUTH);
        setVisible(true);
}
    private void loadProducts() {
        try {
            Connection connection = DBConnection.getConnection();


            String sql = "SELECT * FROM Products";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getBigDecimal("unit_price"),
                        rs.getInt("current_stock"),
                        rs.getInt("reorder_level"),
                        rs.getInt("supplier_id"),

                });
            }

            //connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Products();
    }
}