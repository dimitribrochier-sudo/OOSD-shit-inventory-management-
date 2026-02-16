package org.example;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    //enan 1 issue quand p retourne back a cause sa in met sa la, bizin redresser la
    public Dashboard() {

    }

    public Dashboard(int roleId){
    setTitle("Dashboard");
    setSize(400, 250);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JButton userBtn = new JButton("Users");
    JButton supplierBtn = new JButton("Suppliers");
    JButton productBtn = new JButton("Products");
    JButton logoutBtn = new JButton("Log Out");

        userBtn.addActionListener(e -> new Users());
        supplierBtn.addActionListener(e -> new Suppliers());
        productBtn.addActionListener(e -> new Products());

        logoutBtn.addActionListener(e -> {
            dispose();       // close dashboard
            new Login()      /*go to logon page */ ;});

    setLayout(new GridLayout(3, 1, 10, 10));
    add(userBtn);
    add(supplierBtn);
    add(productBtn);
    add(logoutBtn);

        // Role-based access control
        if (roleId == 1) {
            // Admin: full access, all buttons enabled
            userBtn.setEnabled(true);
            productBtn.setEnabled(true);
            supplierBtn.setEnabled(true);
            logoutBtn.setEnabled(true);

        } else if (roleId == 2) {
            // Limited role: only products and suppliers
            userBtn.setVisible(false); // hide the Users button
            productBtn.setEnabled(true);
            supplierBtn.setEnabled(true);
            logoutBtn.setEnabled(true);

        }
    setVisible(true);
}

}
