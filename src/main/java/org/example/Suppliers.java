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

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        add(backButton,BorderLayout.SOUTH);
        setVisible(true);

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
