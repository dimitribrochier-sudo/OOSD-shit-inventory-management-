package app.inventory_management.views;
import app.inventory_management.controllers.SaleController;
import app.inventory_management.views.ProductScreen;
import org.example.Products;

import javax.swing.*;
import java.awt.*;

public class SalesScreen extends JFrame {

    JComboBox<String> productDropdown;
    JTextField productName;
    JTextField quantity;
    JComboBox<String> customerDropdown;
    JTextField price;
    JTextField total;

    JButton confirmBtn;

    SaleController controller = new SaleController();

    public SalesScreen(){
        setTitle("Sales");
        setSize(400,300);
        setLayout(null);
        setVisible(true);

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

        productName = new JTextField();
        productName.setBounds(150,50,150,25);
        productName.setEditable(false);

        quantity = new JTextField();
        quantity.setBounds(150,80,150,25);

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

        //adding to screen the fields
        add(productDropdown);
        add(productName);
        add(quantity);
        add(customerDropdown);
        add(price);
        add(total);
        add(confirmBtn);

        //adding labels
        add(productLabel);
        add(productNameLabel);
        add(quantityLabel);
        add(customerLabel);
        add(priceLabel);
        add(totalLabel);


        confirmBtn.addActionListener(e -> saveSale());

    }

    private void saveSale() {

        int productId = Integer.parseInt(productDropdown.getSelectedItem().toString());
        int customerId = Integer.parseInt(customerDropdown.getSelectedItem().toString());
        int qty = Integer.parseInt(quantity.getText());
        double pr = Double.parseDouble(price.getText());

        controller.createSale(productId, customerId, qty, pr);

        JOptionPane.showMessageDialog(this,"Sale recorded!");
    }


    public static void main(String[] args){
        new SalesScreen();
    }
}
