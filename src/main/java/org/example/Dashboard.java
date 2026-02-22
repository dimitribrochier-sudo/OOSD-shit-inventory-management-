package org.example;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    //enan 1 issue quand p retourne back a cause sa in met sa la, bizin redresser la
    public Dashboard() {

    }

    public Dashboard(int roleId) {
        setTitle("Dashboard");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Title
        JLabel titleLabel = new JLabel("DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        //banes button la
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 20, 60));
        buttonPanel.setSize(250,60);
        //cree button
        JButton userBtn = new JButton("Users");
        JButton supplierBtn = new JButton("Suppliers");
        JButton productBtn = new JButton("Products");
        JButton customerBtn = new JButton ("Customers");
        JButton inv_transBtn = new JButton("Inventory Transaction");
        JButton logoutBtn = new JButton("Log Out");

        // ajoute button
        buttonPanel.add(userBtn);
        buttonPanel.add(supplierBtn);
        buttonPanel.add(productBtn);
        buttonPanel.add(customerBtn);
        buttonPanel.add(inv_transBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);

        //action banes buttom
        userBtn.addActionListener(e -> new Users());
        supplierBtn.addActionListener(e -> new Suppliers());
        productBtn.addActionListener(e -> new Products());
        customerBtn.addActionListener(e->  JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));
        inv_transBtn.addActionListener(e ->  JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login();
        });

        //role base thingy
        if (roleId == 1) {
            // Admin – full access
            userBtn.setEnabled(true);
            productBtn.setEnabled(true);
            supplierBtn.setEnabled(true);
            inv_transBtn.setEnabled(true);

        } else if (roleId == 2 || roleId == 3) {
            // Limited role
            userBtn.setEnabled(false);
            inv_transBtn.setEnabled(false);
        }

        setVisible(true);

    }
}
