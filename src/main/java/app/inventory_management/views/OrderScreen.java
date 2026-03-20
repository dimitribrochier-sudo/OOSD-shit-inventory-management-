package app.inventory_management.views;

import javax.swing.*;
import app.inventory_management.controllers.OrderController;

public class OrderScreen extends JFrame {
    JComboBox<String> productDropdown;
    JTextField productName;
    JTextField quantity;
    JComboBox<String> supplierDropdown;
    JTextField price;
    JTextField total;

    JButton confirmBtn;

    OrderController controller = new OrderController();

    public OrderScreen(){

        setTitle("Order");
        setSize(400,300);
        setLayout(null);

        productDropdown = new JComboBox<>();
        productDropdown.setBounds(150,20,150,25);

        productName = new JTextField();
        productName.setBounds(150,50,150,25);
        productName.setEditable(false);

        quantity = new JTextField();
        quantity.setBounds(150,80,150,25);

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

        add(productDropdown);
        add(productName);
        add(quantity);
        add(supplierDropdown);
        add(price);
        add(total);
        add(confirmBtn);

        confirmBtn.addActionListener(e -> saveOrder());

        setVisible(true);
    }

    private void saveOrder() {

        int productId = Integer.parseInt(productDropdown.getSelectedItem().toString());
        int supplierId = Integer.parseInt(supplierDropdown.getSelectedItem().toString());
        int qty = Integer.parseInt(quantity.getText());
        double pr = Double.parseDouble(price.getText());

        controller.createOrder(productId, supplierId, qty, pr);

        JOptionPane.showMessageDialog(this,"Order recorded!");
    }

    public static void main(String[] args) {
        OrderScreen orderScreen = new OrderScreen();
    }
}
