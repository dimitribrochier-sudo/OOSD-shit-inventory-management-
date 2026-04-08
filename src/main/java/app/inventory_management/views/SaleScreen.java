package app.inventory_management.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import app.inventory_management.controllers.SaleController;
import app.inventory_management.models.Sale;

public class SaleScreen extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField idField;

    SaleController controller = new SaleController();

    public SaleScreen() {
        setTitle("Sales");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        model = new DefaultTableModel();
        table = new JTable(model);

        // Columns (adjust based on your Sale model later)
        model.addColumn("Sale ID");
        model.addColumn("Product ID");
        model.addColumn("Customer ID");
        model.addColumn("Quantity");
        model.addColumn("Price");
        model.addColumn("Total Price");
        model.addColumn("Date");


        loadSales();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Top container
        JPanel topContainer = new JPanel(new BorderLayout());

        // LEFT SIDE
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        //Finding sale by id
        leftPanel.add(new JLabel("Find Sale by ID:"));
        idField = new JTextField(8);
        leftPanel.add(idField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> findSaleById());
        leftPanel.add(findButton);

        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadSales();
        });
        leftPanel.add(showAllButton);

        // RIGHT SIDE (buttons)
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton makeSaleButton = new JButton("Make Sale");
        makeSaleButton.addActionListener(e -> openMakeSalesScreen());

        rightPanel.add(makeSaleButton);

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

    // Load sales (dummy for now)
    private void loadSales() {
        //laoding data
        List<Sale> saleList = controller.loadSale();
        for (Sale s : saleList) {
            model.addRow(new Object[]{
                    s.getSaleId(),
                    s.getProductId(),
                    s.getCustomerId(),
                    s.getQuantity(),
                    s.getPrice(),
                    s.getTotalPrice(),
                    s.getCreated_at()
            });
        }
    }

    private void findSaleById(){
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Sale ID");
            return;
        }

        Sale foundSale = controller.findSale(Integer.parseInt(input));

        model.setRowCount(0);
        if (foundSale != null){
            model.addRow(new Object[]{
                    foundSale.getSaleId(),
                    foundSale.getProductId(),
                    foundSale.getCustomerId(),
                    foundSale.getQuantity(),
                    foundSale.getPrice(),
                    foundSale.getTotalPrice(),
                    foundSale.getCreated_at()
            });
        }else{
            JOptionPane.showMessageDialog(this, "Sale not Found");
        }

    }

    // Opens new screen instead of dialog
    private void openMakeSalesScreen() {
        dispose();
        new MakeSalesScreen();
    }

}
