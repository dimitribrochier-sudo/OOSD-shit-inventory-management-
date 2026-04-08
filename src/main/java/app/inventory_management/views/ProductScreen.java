package app.inventory_management.views;

import java.util.List;
import app.inventory_management.models.Product;
import app.inventory_management.controllers.ProductController;
import app.inventory_management.controllers.SupplierController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductScreen extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField idField;

    //creating a productController
    ProductController controller = new ProductController();
    SupplierController suppController = new SupplierController();

    public ProductScreen(){
        setTitle("Products");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        this.getContentPane().setBackground(new Color(15, 23, 42));//chnaged bgcolor


        model = new DefaultTableModel();
        table = new JTable(model);

        //for table color
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(new Color(241, 245, 249));
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(new Color(241, 245, 249));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        model.addColumn("Product ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("unit Price");
        model.addColumn("Current Stock");
        model.addColumn("Reorder Level");
        model.addColumn("Supplier ID");

        //populating data
       loadProducts();


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));//changed
        scrollPane.setBorder(BorderFactory.createEmptyBorder());//chnaged
        add(scrollPane, BorderLayout.CENTER);

        //All buttons
        //creating a main top container to separates left and right buttons
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(15, 23, 42));//chnaged

        //Everything on the left side
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(15, 23, 42));

        //chnaged section
        JLabel findLabel = new JLabel("Find By Product ID:");
        findLabel.setForeground(new Color(241, 245, 249)); // Set the color to white

        leftPanel.add(findLabel);
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
            new DashboardScreen();
        });

        //creating the right container
        JPanel rightPanel=new JPanel((new FlowLayout(FlowLayout.RIGHT)));
        rightPanel.setBackground(new Color(15, 23, 42));

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteProduct());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addProduct());

        //edit/update button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e ->  editProduct());

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


    // the functions
    private void loadProducts(){
        //populating data
        List<Product> productList = controller.loadProduct();
        for (Product p : productList) {
            model.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getUnitPrice(),
                    p.getCurrentStock(),
                    p.getReorderLevel(),
                    p.getSupplier_id()
            });
        }
    }

    private void findProductById(){
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Product ID");
            return;
        }

        Product foundProduct = controller.findProduct(Integer.parseInt(input));

        model.setRowCount(0);//clear table
        if (foundProduct != null){
            model.addRow(new Object[]{
                    foundProduct.getProductId(),
                    foundProduct.getName(),
                    foundProduct.getCategory(),
                    foundProduct.getUnitPrice(),
                    foundProduct.getCurrentStock(),
                    foundProduct.getReorderLevel(),
                    foundProduct.getSupplier_id()
            });
        }else {
            JOptionPane.showMessageDialog(this, "Product not found");
        }
    }


    private void deleteProduct(){
        int selectedRow = table.getSelectedRow();

        //Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
            return;
        }

        int productId = (int) model.getValueAt(selectedRow, 0);

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteProduct(productId);
            JOptionPane.showMessageDialog(this, "Product deleted");

            // Remove row from table
            model.removeRow(selectedRow);
        }
    }

    private void addProduct(){
        //create dialog
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        dialog.getContentPane().setBackground(new Color(15, 23, 42));

        // title add product
        JLabel titleLabel = new JLabel("ADD PRODUCT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(241, 245, 249));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        //creating the form
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(new Color(15, 23, 42));
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

        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

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

            //converting categoryEnum to a string
            Product.Category categoryEnum;

            if (name.isEmpty() || category.isEmpty() || unitPriceText.isEmpty() ||
                    currentStockText.isEmpty() || reorderLevelText.isEmpty() || supplierIdText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            try{
                categoryEnum = Product.Category.valueOf(category.toUpperCase());
            }   catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid category!");
                return;
            }

            double unitPrice;
            int currentStock, reorderLevel, supplierId;

            try{
                unitPrice = Double.parseDouble(unitPriceText);
            }   catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Unit Price must be a valid number.");
                return;
            }
            try{
                currentStock = Integer.parseInt(currentStockText);
                reorderLevel = Integer.parseInt(reorderLevelText);
                supplierId = Integer.parseInt(supplierIdText);
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Stock, Reorder Level, and Supplier ID must be valid integers!");
                return;
            }

            if (unitPrice < 0) {
                JOptionPane.showMessageDialog(dialog, "Unit Price cannot be negative!");
                return;
            }
            if (currentStock < 0) {
                JOptionPane.showMessageDialog(dialog, "Current Stock cannot be negative!");
                return;
            }
            if (reorderLevel < 0) {
                JOptionPane.showMessageDialog(dialog, "Reorder Level cannot be negative!");
                return;
            }

            if (suppController.findSupplier(supplierId) == null) {
                JOptionPane.showMessageDialog(dialog, "Supplier doesn't exist, Check Supplier Table");
            }

            if (name.length() < 2 || name.length() > 100){
                JOptionPane.showMessageDialog(dialog, "Name too long or too Short!");
                return;
            }

            Product newProduct = new Product(
                    name,
                    categoryEnum,
                    unitPrice,
                    currentStock,
                    reorderLevel,
                    supplierId);

            controller.addProduct(newProduct);

            JOptionPane.showMessageDialog(dialog, "Product added successfully!");
            dialog.dispose();
            model.setRowCount(0);
            loadProducts();

        });
        for (Component c : formPanel.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(new Color(241, 245, 249));
            }

            if (c instanceof JTextField) {
                c.setBackground(new Color(30, 41, 59));
                c.setForeground(Color.WHITE);
                ((JTextField) c).setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
            }
        }
        dialog.setVisible(true);
        }

    private void editProduct() {

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

        dialog.getContentPane().setBackground(new Color(15, 23, 42));

        // Title
        JLabel titleLabel = new JLabel("EDIT PRODUCT", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(241, 245, 249));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(new Color(15, 23, 42));
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
            // edited editProduct START
            String newName = nameField.getText().trim();
            String newCategory = categoryField.getText().trim();
            String unitPriceText = unitPriceField.getText().trim();
            String currentStockText = currentStockField.getText().trim();
            String reorderLevelText = reorderLevelField.getText().trim();
            String supplierIdText = supplierIdField.getText().trim();

            //converting categoryEnum to a string
            Product.Category categoryEnum;

            if (newName.isEmpty() || newCategory.isEmpty() || unitPriceText.isEmpty() ||
                    currentStockText.isEmpty() || reorderLevelText.isEmpty() || supplierIdText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            try{
                categoryEnum = Product.Category.valueOf(newCategory.toUpperCase());
            }   catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid category!");
                return;
            }

            double newUnitPrice;
            int newCurrentStock, newReorderLevel, newSupplierId;

            try{
                newUnitPrice = Double.parseDouble(unitPriceText);
            }   catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Unit Price must be a valid number.");
                return;
            }
            try{
                newCurrentStock = Integer.parseInt(currentStockText);
                newReorderLevel = Integer.parseInt(reorderLevelText);
                newSupplierId = Integer.parseInt(supplierIdText);
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(dialog, "Stock, Reorder Level, and Supplier ID must be valid integers!");
                return;
            }

            if (newUnitPrice < 0) {
                JOptionPane.showMessageDialog(dialog, "Unit Price cannot be negative!");
                return;
            }
            if (newCurrentStock < 0) {
                JOptionPane.showMessageDialog(dialog, "Current Stock cannot be negative!");
                return;
            }
            if (newReorderLevel < 0) {
                JOptionPane.showMessageDialog(dialog, "Reorder Level cannot be negative!");
                return;
            }

            if (suppController.findSupplier(newSupplierId) == null) {
                JOptionPane.showMessageDialog(dialog, "Supplier doesn't exist, Check Supplier Table");
                return;
            }

            if (newName.length() < 2 || newName.length() > 100){
                JOptionPane.showMessageDialog(dialog, "Name too long or too Short!");
                return;
            }

            Product editedProduct = new Product(
                    newName,
                    categoryEnum,
                    newUnitPrice,
                    newCurrentStock,
                    newReorderLevel,
                    newSupplierId
            );
            //edited edit product END
            controller.editProduct(editedProduct);
            JOptionPane.showMessageDialog(dialog, "Product updated successfully!");
            dialog.dispose();

            // Refresh table
            model.setRowCount(0);
            loadProducts();

        });
        for (Component c : formPanel.getComponents()) {


            if (c instanceof JLabel) {
                c.setForeground(new Color(241, 245, 249));
            }

            if (c instanceof JTextField) {
                c.setBackground(new Color(30, 41, 59));
                c.setForeground(Color.WHITE);
                ((JTextField) c).setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
            }
        }

        dialog.setVisible(true);
    }
}
