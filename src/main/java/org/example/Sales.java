package org.example;

import javax.swing.*;
import java.awt.*;

public class Sales extends JFrame {

    private JComboBox<Products> productComboBox;
    private JLabel stockLabel;
    private JTextField quantityField;
    private JTextField customerIdField;
    private JLabel totalLabel;
    private JButton saveButton;


    public Sales(){

        setTitle("Sales");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        productComboBox = new JComboBox<>();
        stockLabel = new JLabel("0");
        quantityField = new JTextField();
        customerIdField = new JTextField();
        totalLabel = new JLabel("0.0");
        saveButton = new JButton("Save Sale");

        add(new JLabel("Select Product:"));
        add(productComboBox);

        add(new JLabel("Available Stock:"));
        add(stockLabel);

        add(new JLabel("Quantity:"));
        add(quantityField);

        add(new JLabel("Customer ID:"));
        add(customerIdField);

        add(new JLabel("Total Price:"));
        add(totalLabel);

        add(new JLabel(""));
        add(saveButton);

       // loadProducts();
       // addListeners();
    }

    public static void main(String[] args){
        new Sales();
    }

}
