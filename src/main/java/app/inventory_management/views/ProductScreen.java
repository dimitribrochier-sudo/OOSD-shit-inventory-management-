package app.inventory_management.views;

import app.inventory_management.repository.ProductDAO;
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
        findButton.addActionListener(e ->  controller.findProduct(Integer.parseInt(input)));
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


    //redo the functions
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

    }

    private void editProduct(){

    }

}
