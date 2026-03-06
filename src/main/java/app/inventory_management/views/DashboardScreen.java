package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;


public class DashboardScreen extends JFrame {


    public DashboardScreen(){
        String username = "Greg";
        int roleId = 2;

        //Title
        setTitle("Dashboard");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);

        //swing constant control alignment inside component
        JLabel titleLabel = new JLabel("WELCOME " + username, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        //Button Layout
        JPanel buttonPanel = getJPanel();


        //borderLayout controls where components are placed inside the container
        add(buttonPanel, BorderLayout.CENTER);
    }

    //button panel builder
    private static JPanel getJPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 20, 60));
        buttonPanel.setSize(250,60);

        //Building button
        JButton userBtn = new JButton("Users");
        JButton supplierBtn = new JButton("Suppliers");
        JButton productBtn = new JButton("Products");
        JButton salesBtn = new JButton("Sales");
        JButton customerBtn = new JButton ("Customers");
        JButton inv_transBtn = new JButton("Inventory Transaction");
        JButton logoutBtn = new JButton("Log Out");

        //Adding the buttons
        buttonPanel.add(userBtn);
        buttonPanel.add(supplierBtn);
        buttonPanel.add(productBtn);
        buttonPanel.add(salesBtn);
        buttonPanel.add(customerBtn);
        buttonPanel.add(inv_transBtn);
        buttonPanel.add(logoutBtn);
        return buttonPanel;
    }


}
