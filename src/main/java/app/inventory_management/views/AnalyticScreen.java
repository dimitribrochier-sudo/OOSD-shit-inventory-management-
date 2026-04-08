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
        panel.setBorder(BorderFactory.createLineBorder(Color.red));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));

        //adding the cards now
        panel.add(titleLabel,BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel bottomPanel() {
        JPanel panel = new JPanel();

        JButton backBtn = new JButton("Back");

        backBtn.addActionListener(e -> {
            dispose();// closes this screen
            new DashboardScreen();
        });

        panel.add(backBtn);
        return panel;
    }
}





