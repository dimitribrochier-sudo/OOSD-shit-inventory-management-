package org.example;

import org.example.config.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Users extends JFrame{
    JTable table;
    DefaultTableModel model;
    JTextField idField;

    //SETTING FRAME
    public Users() {
        setTitle("Users");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //SETTING THE TABLE
        model = new DefaultTableModel();
        table = new JTable(model);


        // Adding Columns names
        model.addColumn("User ID");
        model.addColumn("Username");
        //model.addColumn("Password");
        model.addColumn("Full Name");
        model.addColumn("Role ID");
        model.addColumn("Created At");

        loadUsers();

        //added the table inside a scrollable panel(vertical)
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        //All buttons
        JPanel topPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));

        //back button--> go back to dashboard(need to fix position)
        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        //find user by id
        JButton findButton;

        topPanel.add(new JLabel("Find By User ID:"));
        idField = new JTextField(8);
        topPanel.add(idField);

        findButton = new JButton("Find");
        findButton.addActionListener(e -> findUserById());

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadUsers();
        });

        //deleting a row
        //tried to put a bin icon but it does not seem to fit in correctly
         /* ImageIcon binIcon= new ImageIcon("C:\\Users\\i\\Documents\\Inventory_Management\\src\\main\\java\\org\\example\\resoures\\images\\bin-svgrepo-com.png");
        Image img = binIcon.getImage();
        Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        binIcon = new ImageIcon(scaledImg); */

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteUser());


        topPanel.add(findButton);
        topPanel.add(showAllButton);
        topPanel.add(deleteButton);
        add(topPanel,BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);
        setVisible(true);




    }
    //ALL FUNCTIONS


    private void deleteUser() {

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
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = DBConnection.getConnection();

                String sql = "DELETE FROM users WHERE user_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "User deleted successfully!");

                // Remove row from table
                model.removeRow(selectedRow);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting user.");
            }
        }
    }

    private void findUserById() {
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a User ID");
            return;
        }

        try {
            //when passing data from frame, the data is ALWAYS in String, so have to do a string conversion, from string to int
            int id = Integer.parseInt(input);

            Connection connection = DBConnection.getConnection();

            String sql = "SELECT user_id, username, full_name, role_id, created_at FROM users WHERE user_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            // clear table first
            model.setRowCount(0);

            if (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        //   rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getInt("role_id"),
                        rs.getTimestamp("created_at")
                });
            } else {
                JOptionPane.showMessageDialog(this, "User not found");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a number");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            Connection connection = DBConnection.getConnection();


            String sql = "SELECT user_id,username,full_name,role_id, created_at FROM users";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("username"),
                     // rs.getString("password_hash"),
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

/* NOTES: password_hash has been commented/removed since i dont want it to appear on the user table(privacy concern)*/