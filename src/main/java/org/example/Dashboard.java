package org.example;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard(int roleId){
    setTitle("Dashboard");
    setSize(400, 250);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JButton userBtn = new JButton("Users");
    JButton supplierBtn = new JButton("Suppliers");
    JButton productBtn = new JButton("Products");

        userBtn.addActionListener(e -> new Users());
        supplierBtn.addActionListener(e -> new Suppliers());
        productBtn.addActionListener(e -> new Products());

    setLayout(new GridLayout(3, 1, 10, 10));
    add(userBtn);
    add(supplierBtn);
    add(productBtn);

        // Role-based access control
        if (roleId == 1) {
            // Admin: full access, all buttons enabled
            userBtn.setEnabled(true);
            productBtn.setEnabled(true);
            supplierBtn.setEnabled(true);
        } else if (roleId == 2) {
            // Limited role: only products and suppliers
            userBtn.setVisible(false); // hide the Users button
            productBtn.setEnabled(true);
            supplierBtn.setEnabled(true);
        }
    setVisible(true);
}


    public Dashboard() {
        
    }
}
