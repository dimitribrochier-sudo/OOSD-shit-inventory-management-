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
    JTextField idField;

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

        //All buttons
        //creating a main top container to separates left and right buttons
        JPanel topContainer = new JPanel(new BorderLayout());

        //Everything on the left side
        JPanel leftPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
        //find product by id
        leftPanel.add(new JLabel("Find By Product ID:"));
        idField = new JTextField(8);
        leftPanel.add(idField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e ->  findProductById());
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadProducts();
        });
        leftPanel.add(showAllButton);

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        //creating the right container
        JPanel rightPanel=new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteProduct());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> AddProduct());

        //edit/update button
        JButton editButton = new JButton("Edit");
       editButton.addActionListener(e ->  EditProduct());

        rightPanel.add(addButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        //adding the left and right in the top container
        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        //adding the top container and backbutton in the frame
        add(topContainer, BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);


        setVisible(true);
}

    //ALLL FUNCTIONS
    private void findProductById() {
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Product ID");
            return;
        }

        try {
            int id = Integer.parseInt(input);

            Connection connection = DBConnection.getConnection();

            String sql = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            // Clear table first
            model.setRowCount(0);

            if (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("unit_price"),
                        rs.getInt("current_stock"),
                        rs.getInt("reorder_level"),
                        rs.getInt("supplier_id")
                });
            } else {
                JOptionPane.showMessageDialog(this, "Product not found");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a number");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching product");
        }
    }

    private void EditProduct() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first.");
            return;
        }

        // Get selected row data
        int productId = (int) model.getValueAt(selectedRow, 0);
        String name = model.getValueAt(selectedRow, 1).toString();
        String category = model.getValueAt(selectedRow, 2).toString();
        double unitPrice = Double.parseDouble(model.getValueAt(selectedRow, 3).toString());
        int currentStock = Integer.parseInt(model.getValueAt(selectedRow, 4).toString());
        int reorderLevel = Integer.parseInt(model.getValueAt(selectedRow, 5).toString());
        int supplierId = Integer.parseInt(model.getValueAt(selectedRow, 6).toString());

        // Create dialog
        JDialog dialog = new JDialog(this, "Edit Product", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("EDIT PRODUCT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nameField = new JTextField(name);
        JTextField categoryField = new JTextField(category);
        JTextField unitPriceField = new JTextField(String.valueOf(unitPrice));
        JTextField currentStockField = new JTextField(String.valueOf(currentStock));
        JTextField reorderLevelField = new JTextField(String.valueOf(reorderLevel));
        JTextField supplierIdField = new JTextField(String.valueOf(supplierId));

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Unit Price:"));
        formPanel.add(unitPriceField);

        formPanel.add(new JLabel("Current Stock:"));
        formPanel.add(currentStockField);

        formPanel.add(new JLabel("Reorder Level:"));
        formPanel.add(reorderLevelField);

        formPanel.add(new JLabel("Supplier ID:"));
        formPanel.add(supplierIdField);

        formPanel.add(updateButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Cancel button
        cancelButton.addActionListener(e -> dialog.dispose());

        // Update button
        updateButton.addActionListener(e -> {

            String newName = nameField.getText().trim();
            String newCategory = categoryField.getText().trim();
            String unitPriceText = unitPriceField.getText().trim();
            String currentStockText = currentStockField.getText().trim();
            String reorderLevelText = reorderLevelField.getText().trim();
            String supplierIdText = supplierIdField.getText().trim();

            if (newName.isEmpty() || newCategory.isEmpty() || unitPriceText.isEmpty() ||
                    currentStockText.isEmpty() || reorderLevelText.isEmpty() || supplierIdText.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            try {
                double newUnitPrice = Double.parseDouble(unitPriceText);
                int newCurrentStock = Integer.parseInt(currentStockText);
                int newReorderLevel = Integer.parseInt(reorderLevelText);
                int newSupplierId = Integer.parseInt(supplierIdText);

                Connection connection = DBConnection.getConnection();

                String sql = "UPDATE products SET name=?, category=?, unit_price=?, current_stock=?, reorder_level=?, supplier_id=? WHERE product_id=?";
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, newName);
                ps.setString(2, newCategory);
                ps.setDouble(3, newUnitPrice);
                ps.setInt(4, newCurrentStock);
                ps.setInt(5, newReorderLevel);
                ps.setInt(6, newSupplierId);
                ps.setInt(7, productId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Product updated successfully!");
                dialog.dispose();

                // Refresh table
                model.setRowCount(0);
                loadProducts();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Unit Price must be decimal and others must be numbers!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error updating product.");
            }
        });

        dialog.setVisible(true);
    }
    //Delete a product
    private void deleteProduct() {

        int selectedRow = table.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
            return;
        }

        // Get user_id from column 0
        int userId = (int) model.getValueAt(selectedRow, 0);

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = DBConnection.getConnection();

                String sql = "DELETE FROM products WHERE product_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Product deleted successfully!");

                // Remove row from table
                model.removeRow(selectedRow);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting product.");
            }
        }
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