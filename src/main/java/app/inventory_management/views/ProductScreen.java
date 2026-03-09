package app.inventory_management.views;

import app.inventory_management.repository.ProductDAO;
import app.inventory_management.models.Product;
import app.inventory_management.controllers.ProductController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;




public class ProductScreen extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField idField;

    public ProductScreen(){
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



        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);


    }

}
