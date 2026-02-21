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


        loadProducts();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> AddProduct());

        topPanel.add(addButton);
        add(backButton,BorderLayout.SOUTH);
        add(topPanel,BorderLayout.NORTH);
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

    private  void AddProduct(){
        //create dialog
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // title add product
        JLabel titleLabel = new JLabel("ADD PRODUCT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        //creating the form
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField unitPriceField = new JTextField();
        JTextField currentStockField = new JTextField();
        JTextField reorderLevelField = new JTextField();
        JTextField supplierIdField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Catergory:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Unit Price:"));
        formPanel.add(unitPriceField);

        formPanel.add(new JLabel("Current Stock:"));
        formPanel.add(currentStockField);

        formPanel.add(new JLabel("Reorder Level:"));
        formPanel.add(reorderLevelField);

        formPanel.add(new JLabel("Supplier ID:"));
        formPanel.add(supplierIdField);


        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        //Button save and cancel
        //cancel
        cancelButton.addActionListener(e -> dialog.dispose());

        //save
        saveButton.addActionListener(e -> {
            // Get values from fields
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String unitPriceText = unitPriceField.getText().trim();
            String currentStockText = currentStockField.getText().trim();
            String reorderLevelText = reorderLevelField.getText().trim();
            String supplierIdText = supplierIdField.getText().trim();

            if (name.isEmpty() || category.isEmpty() || unitPriceText.isEmpty() ||
                    currentStockText.isEmpty() || reorderLevelText.isEmpty() || supplierIdText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            try {
                double unitPrice = Double.parseDouble(unitPriceText);
                int currentStock = Integer.parseInt(currentStockText);
                int reorderLevel = Integer.parseInt(reorderLevelText);
                int supplierId = Integer.parseInt(supplierIdText);

                Connection connection = DBConnection.getConnection();

                String sql = "INSERT INTO products (name, category, unit_price, current_stock, reorder_level, supplier_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, name);
                ps.setString(2, category);
                ps.setDouble(3, unitPrice);
                ps.setInt(4, currentStock);
                ps.setInt(5, reorderLevel);
                ps.setInt(6, supplierId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Product added successfully!");
                dialog.dispose();

                model.setRowCount(0);
                loadProducts();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Unit Price, Stock, Reorder Level, and Supplier ID must be numbers!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding product.");
            }
        });

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new Products();
    }
}