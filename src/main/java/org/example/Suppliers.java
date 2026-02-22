package org.example;

import org.example.config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Suppliers extends JFrame {
    JTable table;
    DefaultTableModel model;
    JTextField idField;

    public Suppliers() {
        setTitle("Suppliers");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Supplier ID");
        model.addColumn("Name");
        model.addColumn("Contact NUmber");
        model.addColumn("Email");
        model.addColumn("Address");
        model.addColumn("Created At");

        loadSuppliers();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        //All buttons
        //creating a main top container to separates left and right buttons
        JPanel topContainer = new JPanel(new BorderLayout());

        //Everything on the left side
        JPanel leftPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
        //find product by id
        leftPanel.add(new JLabel("Find By Supplier ID:"));
        idField = new JTextField(8);
        leftPanel.add(idField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadSuppliers();
        });
        leftPanel.add(showAllButton);

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        //creating the right container
        JPanel rightPanel=new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e ->  deleteSupplier());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e ->  JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));

        //edit/update button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e ->  JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!"));

        rightPanel.add(addButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        //adding the left and right in the top container
        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        //adding the top container and backbutton in the frame
        add(topContainer, BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);

        setVisible(true);

    }

    private void deleteSupplier() {

        int selectedRow = table.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
            return;
        }

        // Get user_id from column 0
        int userId = (int) model.getValueAt(selectedRow, 0);

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this supplier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = DBConnection.getConnection();

                String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "suppplier deleted successfully!");

                // Remove row from table
                model.removeRow(selectedRow);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting supplier.");
            }
        }
    }

    private void loadSuppliers() {
        try {
            Connection connection = DBConnection.getConnection();


            String sql = "SELECT * FROM Suppliers";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getTimestamp("created_at")
                });
            }

            //connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Suppliers();
    }
    }
