package app.inventory_management.views;

import app.inventory_management.controllers.OrderController;
import app.inventory_management.models.Supplier;
import app.inventory_management.models.Product;
import app.inventory_management.controllers.ProductController;
import app.inventory_management.utils.Calculator;
import app.inventory_management.controllers.SupplierController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MakeOrderScreen extends JFrame {

    JComboBox<Integer> productDropdown;
    JTextField productName;
    JTextField quantity;
    JComboBox<Integer> supplierDropdown;
    JTextField price;
    JTextField total;

    JButton confirmBtn;
    JButton returnBtn;

    OrderController orderController = new OrderController();
    ProductController productController = new ProductController();
    Calculator calculate = new Calculator();
    SupplierController supplierController = new SupplierController();

    private int currentStock = 0;
    private double currentPrice = 0.0;

    public MakeOrderScreen(){
        setTitle("Orders");
        setSize(400,350);
        setLayout(null);
        setResizable(false);
        //adding labels objects to fields and combos

        JLabel productLabel;
        JLabel productNameLabel;
        JLabel quantityLabel;
        JLabel supplierLabel;
        JLabel priceLabel;
        JLabel totalLabel;

        // Labels
        productLabel = new JLabel("Product ID: ");
        productLabel.setBounds(20,20,120,25);

        productNameLabel = new JLabel("Product Name: ");
        productNameLabel.setBounds(20, 50, 120, 25);

        quantityLabel = new JLabel("Quantity: ");
        quantityLabel.setBounds(20, 80, 120, 25);

        supplierLabel = new JLabel("Supplier ID: ");
        supplierLabel.setBounds(20, 110, 120, 25);

        priceLabel = new JLabel("Price: ");
        priceLabel.setBounds(20, 140, 120, 25);

        totalLabel = new JLabel("Total: ");
        totalLabel.setBounds(20, 170, 120, 25);

        // Inputs
        productDropdown = new JComboBox<>();
        productDropdown.setBounds(150,20,150,25);
        productDropdown.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadProducts();
            }
        });

        productName = new JTextField();
        productName.setBounds(150,50,150,25);
        productName.setEditable(false);

        quantity = new JTextField("1");
        quantity.setBounds(150,80,150,25);

        quantity.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTotalField();
            }
        });

        supplierDropdown = new JComboBox<>();
        supplierDropdown.setBounds(150,110,150,25);

        price = new JTextField();
        price.setBounds(150,140,150,25);
        price.setEditable(false);

        total = new JTextField();
        total.setBounds(150,170,150,25);
        total.setEditable(false);

        confirmBtn = new JButton("Confirm");
        confirmBtn.setBounds(150,210,100,30);

        returnBtn = new JButton("Return");
        returnBtn.setBounds(150, 245, 100, 30);

        // Add components
        add(productDropdown);
        add(productName);
        add(quantity);
        add(supplierDropdown);
        add(price);
        add(total);
        add(confirmBtn);
        add(returnBtn);

        add(productLabel);
        add(productNameLabel);
        add(quantityLabel);
        add(supplierLabel);
        add(priceLabel);
        add(totalLabel);

        confirmBtn.addActionListener(e -> {
            saveOrder();
            new OrderScreen();
            dispose();
        });

        returnBtn.addActionListener(e -> {
            new OrderScreen();
            dispose();
        });

        loadDropDown();
        setVisible(true);
    }

    private void loadDropDown() {

        // Load suppliers
        List<Integer> supplierIds = new ArrayList<>();
        List<Supplier> suppliers = supplierController.loadSupplier();

        for (Supplier s : suppliers) {
            supplierIds.add(s.getSupplier_id());
        }

        for (Integer id : supplierIds){
            supplierDropdown.addItem(id);
        }

        // Load products
        List<Integer> productIds = new ArrayList<>();
        List<Product> products = productController.loadProduct();

        for(Product p : products) {
            productIds.add(p.getProductId());
        }

        for (Integer id : productIds){
            productDropdown.addItem(id);
        }
    }

    private void saveOrder() {

        int productId = Integer.parseInt(productDropdown.getSelectedItem().toString());
        int supplierId = Integer.parseInt(supplierDropdown.getSelectedItem().toString());
        String qtyText = quantity.getText().trim();
        double pr = Double.parseDouble(price.getText());

        int qty;

        try{
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Only numbers!" );
            return;
        }

        if(qty < 0){
            JOptionPane.showMessageDialog(this, "Only Positive values needed!");
            return;
        }

        orderController.createOrder(productId, supplierId, qty, pr);

        JOptionPane.showMessageDialog(this,"Order recorded!");
    }

    private void loadProducts(){
        if (productDropdown.getSelectedItem() == null) return;

        try{
            int productId = (Integer) productDropdown.getSelectedItem();
            Product product = productController.findProduct(productId);

            if (product != null){
                productName.setText(product.getName());
                currentPrice = product.getUnitPrice();
                currentStock = product.getCurrentStock();

                price.setText(String.format("%.2f", currentPrice));
                quantity.setText("1");

                updateTotalField();
            }
        }catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading product: " + ex.getMessage());
        }
    }

    private void updateTotalField() {
        try {
            int qty = Integer.parseInt(quantity.getText().trim());
            if (qty < 1) throw new IllegalArgumentException("Positive Only");

            double result = calculate.updateTotal(qty, currentPrice);
            total.setText(String.format("%.2f", result));
        }catch(NumberFormatException e){
            total.setText("1");
        }
    }
}