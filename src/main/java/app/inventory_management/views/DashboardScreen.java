package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;
import app.inventory_management.models.User;
import app.inventory_management.utils.SessionManager;


public class DashboardScreen extends JFrame {

    public DashboardScreen(){
        //check session
        if (!SessionManager.getInstance().isLoggedIn()) {
            new LoginScreen();
            return;
        }

        //getuser
        User currentUser = SessionManager.getInstance().getCurrentUser();

        //Title
        setTitle("Dashboard");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setVisible(true);

        //swing constant control alignment inside component
        JLabel titleLabel = new JLabel("WELCOME " + currentUser.getUsername(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titleLabel, BorderLayout.NORTH);

        //Button Layout
        JPanel buttonPanel = getJPanel();


        //borderLayout controls where components are placed inside the container
        add(buttonPanel, BorderLayout.CENTER);


    }

    //button panel builder
    private JPanel getJPanel() {
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

        //action to buttons
        userBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));
        supplierBtn.addActionListener(e -> new SupplierScreen());
        productBtn.addActionListener(e -> new ProductScreen());
        salesBtn.addActionListener(e -> new SalesScreen());
        customerBtn.addActionListener(e -> new CustomerScreen());
        inv_transBtn.addActionListener(e -> JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));
        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            dispose();
            new LoginScreen();
        });

        return buttonPanel;


    }




}
