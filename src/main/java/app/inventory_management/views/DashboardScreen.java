package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;
import app.inventory_management.models.User;
import app.inventory_management.utils.SessionManager;


public class DashboardScreen extends JFrame {

    public DashboardScreen(){
        this.getContentPane().setBackground(new Color(15, 23, 42));//bg color

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
        setResizable(false);


        //swing constant control alignment inside component
        JLabel titleLabel = new JLabel("WELCOME " + currentUser.getUsername(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(241, 245, 249));
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
        buttonPanel.setBackground(new Color(30, 41, 59));

        //Building button
        JButton userBtn = new JButton("Users");
        JButton supplierBtn = new JButton("Suppliers");
        JButton productBtn = new JButton("Products");
        JButton salesBtn = new JButton("Sales");
        JButton orderBtn = new JButton("Orders");
        JButton customerBtn = new JButton ("Customers");
        JButton analyticsBtn = new JButton("Analytics");
        JButton logoutBtn = new JButton("Log Out");


        //Adding the buttons
        buttonPanel.add(userBtn);
        buttonPanel.add(supplierBtn);
        buttonPanel.add(productBtn);
        buttonPanel.add(salesBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(customerBtn);
        buttonPanel.add(analyticsBtn);
        buttonPanel.add(logoutBtn);

        //role base thingy

        User user = SessionManager.getInstance().getCurrentUser();
        if (user.getRoleId() == 1){
            // Admin – full access
            userBtn.setEnabled(true);
            productBtn.setEnabled(true);
            salesBtn.setEnabled(true);
            supplierBtn.setEnabled(true);
        }

        else if(user.getRoleId() == 2){
            // Limited role
            userBtn.setEnabled(false);
            analyticsBtn.setEnabled(false);
        }

        //action to buttons
        userBtn.addActionListener(e -> {
            dispose();
            new UserScreen();
        });

        supplierBtn.addActionListener(e -> {
            dispose();
            new SupplierScreen();
        });

        productBtn.addActionListener(e -> {
            dispose();
            new ProductScreen();
        });

        salesBtn.addActionListener(e -> {
            dispose();
            new SaleScreen();
        });

        orderBtn.addActionListener(e-> {
            dispose();
            new OrderScreen();
        });

        customerBtn.addActionListener(e -> {
            dispose();
            new CustomerScreen();
        });

        analyticsBtn.addActionListener(e-> {
            dispose();
            new AnalyticScreen();
        });

        logoutBtn.addActionListener(e -> {
            SessionManager.getInstance().logout();
            dispose();
            new LoginScreen();
        });

        return buttonPanel;
    }
}
