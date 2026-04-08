package app.inventory_management.views;

import app.inventory_management.controllers.SaleController;
import app.inventory_management.models.Customer;
import app.inventory_management.models.Product;
import app.inventory_management.controllers.ProductController;
import app.inventory_management.utils.Calculator;
import app.inventory_management.controllers.CustomerController;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class MakeSalesScreen extends JFrame {

    JComboBox<Integer> productDropdown;
    JTextField productName;
    JTextField quantity;
    JComboBox<Integer> customerDropdown;
    JTextField price;
    JTextField total;

    JButton confirmBtn;
    JButton returnBtn;

    SaleController saleController = new SaleController();
    ProductController productController = new ProductController();
    Calculator calculate = new Calculator();
    CustomerController customerController = new CustomerController();


    private int currentStock = 0; //track stock
    private double currentPrice = 0.0;

    public MakeSalesScreen(){
        setTitle("Sales");
        setSize(400,350);
        setLayout(null);
        setResizable(false);

        this.getContentPane().setBackground(new Color(15, 23, 42));

        //adding labels objects to fields and combos

        JLabel productLabel;
        JLabel productNameLabel;
        JLabel quantityLabel;
        JLabel customerLabel;
        JLabel priceLabel;
        JLabel totalLabel;

        //adding labels
        productLabel = new JLabel("Product ID: ");
        productLabel.setBounds(20,20,120,25);

        productNameLabel = new JLabel("Product Name: ");
        productNameLabel.setBounds(20, 50, 120, 25);

        quantityLabel = new JLabel("Quantity: ");
        quantityLabel.setBounds(20, 80, 120, 25);

        customerLabel = new JLabel("Customer ID: ");
        customerLabel.setBounds(20, 110, 120, 25);

        priceLabel = new JLabel("Price: ");
        priceLabel.setBounds(20, 140, 120, 25);

        totalLabel = new JLabel("Total: ");
        totalLabel.setBounds(20, 170, 120, 25);


        //input fields

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

        quantity = new JTextField("1"); //default count
        quantity.setBounds(150,80,150,25);

        //listens to change to quantity so that it updates the final total price
        quantity.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTotalField();
            }
        });

        customerDropdown = new JComboBox<>();
        customerDropdown.setBounds(150,110,150,25);

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

        //adding to screen the fields
        add(productDropdown);
        add(productName);
        add(quantity);
        add(customerDropdown);
        add(price);
        add(total);
        add(confirmBtn);
        add(returnBtn);

        //adding labels
        add(productLabel);
        add(productNameLabel);
        add(quantityLabel);
        add(customerLabel);
        add(priceLabel);
        add(totalLabel);


        confirmBtn.addActionListener(e -> {
            saveSale();
            new MakeSalesScreen();
            dispose();
        });

        returnBtn.addActionListener(e -> {
            new SaleScreen();
            dispose();
        });

        loadDropDown();

        this.getContentPane().setBackground(new Color(15, 23, 42));

        //added
        for (Component c : this.getContentPane().getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(new Color(241, 245, 249));
            }
            if (c instanceof JTextField) {
                c.setBackground(new Color(30, 41, 59));
                c.setForeground(Color.WHITE);

                ((JTextField) c).setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
            }
        }
        setVisible(true);

    }

    private void loadDropDown() {

        //take teh loadcustomers, and store id's in a new arrray list.
        List<Integer> idCustomerList = new ArrayList<>();
        List<Customer> customers = customerController.loadCustomer(); // get full list
        for (Customer c : customers) {
            idCustomerList.add(c.getCustomer_id());
        }

        //add them to the dropdownlist
        for (Integer id: idCustomerList){
            customerDropdown.addItem(id);
        }

        //take the loadproducts, and store the id's in a new array list.
        List<Integer> idProductList = new ArrayList<>();
        List<Product> products = productController.loadProduct();
        for(Product p : products) {
            idProductList.add(p.getProductId());
        }

        //add them to the dropdownlist
        for (Integer id: idProductList){
            productDropdown.addItem(id);
        }

    }

    private void saveSale() {

        updateTotalField();

        int productId = Integer.parseInt(productDropdown.getSelectedItem().toString());
        int customerId = Integer.parseInt(customerDropdown.getSelectedItem().toString());
        int qty = Integer.parseInt(quantity.getText().trim());
        double pr = Double.parseDouble(price.getText());

        saleController.createSale(productId, customerId, qty, pr);

        JOptionPane.showMessageDialog(this,"Sale recorded!");

    }

    //when selected it autofills

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
            //validating
            int qty = Integer.parseInt(quantity.getText().trim());
            if (qty < 1) {
                total.setText("QTY MUST > 0");
                total.setForeground(Color.RED);
                return;
            }
            //total
            double result = calculate.updateTotal(qty, currentPrice);
            total.setText(String.format("%.2f", result));
            total.setForeground(Color.WHITE);

        }catch(NumberFormatException e){
            total.setText("Invalid");
            total.setForeground(Color.DARK_GRAY);
        }
        catch (Exception e) {
            total.setText("oops!");
            total.setForeground(Color.cyan);
        }
    }

}
