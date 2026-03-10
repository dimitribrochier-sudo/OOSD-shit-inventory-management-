package app.inventory_management.views;

import app.inventory_management.models.Product;
import app.inventory_management.controllers.ProductController;
import app.inventory_management.models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductScreen extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField idField;

    //same user
    private User user;

    //creating a productController
    ProductController controller = new ProductController();

    public ProductScreen(){
        setTitle("Products");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Product ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("unit Price");
        model.addColumn("Current Stock");
        model.addColumn("Reorder Level");
        model.addColumn("Supplier ID");

        //populating data
        controller.loadProduct();


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
        //clean input
        String input = idField.getText().trim();
        //convert to int
        findButton.addActionListener(e ->  findProductById());
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            controller.loadProduct();
        });
        leftPanel.add(showAllButton);

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new DashboardScreen(user);
        });

        //creating the right container
        JPanel rightPanel=new JPanel((new FlowLayout(FlowLayout.RIGHT)));

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
    }


    // the functions
    private void findProductById(){
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Product ID");
            return;
        }

        Product foundProduct = controller.findProduct(Integer.parseInt(input));

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

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteProduct(selectedRow);
            model.removeRow(selectedRow);

            JOptionPane.showMessageDialog(this, "Product deleted");
        }

    }

    private void addProduct(){
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

            try{
                categoryEnum = Product.Category.valueOf(category.toUpperCase());
            }   catch(IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid category!");
                return;
            }

            if (name.isEmpty() || category.isEmpty() || unitPriceText.isEmpty() ||
                    currentStockText.isEmpty() || reorderLevelText.isEmpty() || supplierIdText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            Product newProduct = new Product(
                    name,
                    categoryEnum,
                    Double.parseDouble(unitPriceText),
                    Integer.parseInt(currentStockText),
                    Integer.parseInt(reorderLevelText),
                    Integer.parseInt(supplierIdText));

            controller.addProduct(newProduct);

            JOptionPane.showMessageDialog(dialog, "Product added successfully!");
            dialog.dispose();
            model.setRowCount(0);
            controller.loadProduct();

        });
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
        dialog.setVisible(true);

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

            //converting categoryEnum to a string
            Product.Category categoryEnum;

            try {
                categoryEnum = Product.Category.valueOf(newCategory.toUpperCase());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid category!");
                return;
            }

            if (newName.isEmpty() || newCategory.isEmpty() || unitPriceText.isEmpty() ||
                    currentStockText.isEmpty() || reorderLevelText.isEmpty() || supplierIdText.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            Product editedProduct = new Product(
                    productId,
                    newName,
                    categoryEnum,
                    unitPrice,
                    currentStock,
                    reorderLevel,
                    supplierId);

            controller.editProduct(editedProduct);
            JOptionPane.showMessageDialog(dialog, "Product updated successfully!");
            dialog.dispose();

            // Refresh table
            model.setRowCount(0);
            controller.loadProduct();

        });
    }

    public static void main(String[] args){new ProductScreen();}
}
