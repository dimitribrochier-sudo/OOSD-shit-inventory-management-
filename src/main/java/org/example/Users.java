package org.example;
import org.example.config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Users extends JFrame{
    JTable table;
    DefaultTableModel model;

    public Users() {
        setTitle("Users");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);


        // define columns
        model.addColumn("User ID");
        model.addColumn("Username");
        model.addColumn("Password");
        model.addColumn("Full Name");
        model.addColumn("Role ID");
        model.addColumn("Created At");

        loadUsers();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });
        add(backButton, BorderLayout.NORTH);

        setVisible(true);

    }

    private void loadUsers() {
        try {
            Connection connection = DBConnection.getConnection();


            String sql = "SELECT * FROM users";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getInt("role_id"),
                        rs.getTimestamp("created_at")
                });
            }

            //connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Users();
    }


}
