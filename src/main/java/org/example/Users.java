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
        model.addColumn("Password");
        model.addColumn("Full Name");
        model.addColumn("Role ID");
        model.addColumn("Created At");

        loadUsers();

        //added the table inside a scrollable panel(vertical)
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        //All buttons
        JPanel topPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));

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

        //back button--> go back to dashboard(need to fix position)
        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard();
        });

        //deleting a row
        //tried to put a bin icon but it does not seem to fit in correctly
         /* ImageIcon binIcon= new ImageIcon("C:\\Users\\i\\Documents\\Inventory_Management\\src\\main\\java\\org\\example\\resoures\\images\\bin-svgrepo-com.png");
        Image img = binIcon.getImage();
        Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        binIcon = new ImageIcon(scaledImg); */

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteUser());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> AddUser());

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> EditUser());

        topPanel.add(findButton);
        topPanel.add(showAllButton);
        topPanel.add(deleteButton);
        topPanel.add(addButton);
        topPanel.add(editButton);
        add(topPanel,BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);
        setVisible(true);

    }


    //ALL FUNCTIONS
    //Create a user
    private  void AddUser(){
        //create dialog
    JDialog dialog = new JDialog(this, "Add User", true);
    dialog.setSize(400, 350);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    // title add user
    JLabel titleLabel = new JLabel("ADD USER", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
    dialog.add(titleLabel, BorderLayout.NORTH);

    //creating the form
    JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

    JTextField usernameField = new JTextField();
    JTextField fullNameField = new JTextField();
    JTextField passwordField = new JTextField();
    JTextField roleIdField = new JTextField();

    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    formPanel.add(new JLabel("Username:"));
    formPanel.add(usernameField);

    formPanel.add(new JLabel("Full Name:"));
    formPanel.add(fullNameField);

    formPanel.add(new JLabel("Password:"));
    formPanel.add(passwordField);

    formPanel.add(new JLabel("Role ID:"));
    formPanel.add(roleIdField);

    formPanel.add(saveButton);
    formPanel.add(cancelButton);

    dialog.add(formPanel, BorderLayout.CENTER);

    //Button save and cancel
        //cancel
    cancelButton.addActionListener(e -> dialog.dispose());

    //save
    saveButton.addActionListener(e -> {

        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleIdField.getText().trim();

        if (username.isEmpty() || fullName.isEmpty() ||
                password.isEmpty() || role.isEmpty()) {

            JOptionPane.showMessageDialog(dialog, "All fields are required!");
            return;
        }

        try {
            //role id should be converted into String to pass into the database,\
            int roleId = Integer.parseInt(role);

            Connection connection = DBConnection.getConnection();

            String sql = "INSERT INTO users (username, full_name, password_hash, role_id, created_at) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, fullName);
            ps.setString(3, password);
            ps.setInt(4, roleId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(dialog, "User added successfully!");

            dialog.dispose();

            // Refresh table
            model.setRowCount(0);
            loadUsers();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Role ID must be a number");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error adding user.");
        }
    });

    dialog.setVisible(true);
}

    private void EditUser() {


        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return;
        }

        // Get selected row data
        int userId = (int) model.getValueAt(selectedRow, 0);
        String username = model.getValueAt(selectedRow, 1).toString();
        String fullName = model.getValueAt(selectedRow, 2).toString();
        String password = model.getValueAt(selectedRow, 3).toString();
        int roleId = (int) model.getValueAt(selectedRow, 4);

        // Create dialog
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        //Ttile edit user
        JLabel titleLabel = new JLabel("EDIT USER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        //creating the form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField usernameField = new JTextField(username);
        JTextField fullNameField = new JTextField(fullName);
        JTextField passwordField = new JTextField(password);
        JTextField roleIdField = new JTextField(String.valueOf(roleId));

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(fullNameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role ID:"));
        formPanel.add(roleIdField);

        formPanel.add(updateButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        //Buttons
        //Cancel form
        cancelButton.addActionListener(e -> dialog.dispose());

        //update/save form
        updateButton.addActionListener(e -> {

            String newUsername = usernameField.getText().trim();
            String newFullName = fullNameField.getText().trim();
            String newPassword = passwordField.getText().trim();
            String roleText = roleIdField.getText().trim();

            if (newUsername.isEmpty() || newPassword.isEmpty() ||
                    newFullName.isEmpty() || roleText.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            try {
                int newRoleId = Integer.parseInt(roleText);

                Connection connection = DBConnection.getConnection();

                String sql = "UPDATE users SET username=?,  full_name=?, password_hash=?, role_id=? WHERE user_id=?";
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, newUsername);
                ps.setString(2, newFullName);
                ps.setString(3, newPassword);
                ps.setInt(4, newRoleId);
                ps.setInt(5, userId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "User updated successfully!");

                dialog.dispose();

                // Refresh table
                model.setRowCount(0);
                loadUsers();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Role ID must be a number");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error updating user.");
            }
        });

        dialog.setVisible(true);
    }


    //Delete a user
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

    //find a user by its ID
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

    //load the table
    private void loadUsers() {
        try {
            Connection connection = DBConnection.getConnection();


           // String sql = "SELECT user_id,username,full_name,role_id, created_at FROM users";
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

/* NOTES: password_hash has been commented/removed since i dont want it to appear on the user table(privacy concern)*/
//21-02-->had to add password column back because of the Create function, we will need to find a way to encrypte and hash it