package app.inventory_management.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import app.inventory_management.controllers.OrderController;
import app.inventory_management.models.Order;



public class OrderScreen extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    OrderController controller = new OrderController();

    public OrderScreen() {
        setTitle("Orders");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        model = new DefaultTableModel();
        table = new JTable(model);

        //for table color
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(new Color(241, 245, 249));
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(new Color(241, 245, 249));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Columns
        model.addColumn("Order ID");
        model.addColumn("Product ID");
        model.addColumn("Supplier ID");
        model.addColumn("Quantity");
        model.addColumn("Price");
        model.addColumn("Total Price");
        model.addColumn("Date");

        loadOrders();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));//changed
        scrollPane.setBorder(BorderFactory.createEmptyBorder());//chnaged
        add(scrollPane, BorderLayout.CENTER);

        // Top container
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(15, 23, 42));//chnaged

        // LEFT SIDE
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(new Color(15, 23, 42));

        JLabel findLabel = new JLabel("Find By Order ID:");
        findLabel.setForeground(new Color(241, 245, 249)); // Set the color to white

        leftPanel.add(findLabel);
        idField = new JTextField(8);
        leftPanel.add(idField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> findOrderById());
        leftPanel.add(findButton);

        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadOrders();
        });
        leftPanel.add(showAllButton);

        // RIGHT SIDE
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(15, 23, 42));

        JButton makeOrderButton = new JButton("Make Order");
        makeOrderButton.addActionListener(e -> openMakeOrderScreen());

        rightPanel.add(makeOrderButton);

        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        add(topContainer, BorderLayout.NORTH);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new DashboardScreen();
        });

        add(backButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Load Orders
    private void loadOrders() {
        List<Order> orderList = controller.loadOrders();

        for (Order o : orderList) {
            model.addRow(new Object[]{
                    o.getOrderId(),
                    o.getProductId(),
                    o.getSupplierId(),
                    o.getQuantity(),
                    o.getPrice(),
                    o.getTotalPrice(),
                    o.getCreated_at()
            });
        }
    }

    private void findOrderById() {
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an Order ID");
            return;
        }

        Order foundOrder = controller.findOrder(Integer.parseInt(input));

        model.setRowCount(0);

        if (foundOrder != null) {
            model.addRow(new Object[]{
                    foundOrder.getOrderId(),
                    foundOrder.getProductId(),
                    foundOrder.getSupplierId(),
                    foundOrder.getQuantity(),
                    foundOrder.getPrice(),
                    foundOrder.getTotalPrice(),
                    foundOrder.getCreated_at()
            });
        } else {
            JOptionPane.showMessageDialog(this, "Order not found");
        }
    }

    // Open Make Order screen
    private void openMakeOrderScreen() {
        dispose();
        new MakeOrderScreen();
    }
}
