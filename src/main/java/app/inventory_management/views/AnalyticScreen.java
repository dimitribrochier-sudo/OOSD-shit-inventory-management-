package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;
import app.inventory_management.utils.BarChartPanel;
import app.inventory_management.controllers.SaleController;

public class AnalyticScreen extends JFrame{

    SaleController controller = new SaleController();

    //taken from youtube videos and W3Schools.

    public AnalyticScreen(){
        setTitle("Analytics Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        this.getContentPane().setBackground(new Color(15, 23, 42));

        //TEH Summary of sales ig
        add(summaryPanel(), BorderLayout.NORTH);

        //adding the barchart
        add(new BarChartPanel(controller.salesPerProduct()), BorderLayout.CENTER);

        //add bottom panel
        add(bottomPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel summaryPanel(){

        //setting the data
        String totalSales = String.valueOf(controller.getTotalSales());
        String totalOrders = String.valueOf(controller.getTotalOrders());
        String totalQuantity = String.valueOf(controller.getTotalQuantity());
        String topProduct = controller.getTopProduct();


        //this is a panel holding cards
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.setOpaque(false);

        //adding them heh
        panel.add(card("Total sales", totalSales));
        panel.add(card("Orders", totalOrders));
        panel.add(card("Items Sold", totalQuantity));
        panel.add(card("Best Product", topProduct));

        return panel;
    }

    private JPanel card(String title, String value){
        //Makin cards
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 41, 59));
        panel.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(148, 163, 184));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        //adding the cards now
        panel.add(titleLabel,BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel bottomPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(15, 23, 42));

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(30, 41, 59));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        backBtn.addActionListener(e -> {
            dispose();// closes this screen
            new DashboardScreen();
        });

        panel.add(backBtn);
        return panel;
    }
}





