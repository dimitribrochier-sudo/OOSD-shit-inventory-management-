package org.example;

import org.example.config.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Customer extends JFrame {

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    //SETTING FRAME
    public Customer() {
        setTitle("Customer");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //SETTING THE TABLE
        model = new DefaultTableModel();
        table = new JTable(model);

        //Adding Columns names
        model.addColumn("Customer ID");
        model.addColumn("Name");
        model.addColumn("Contact Number");
        model.addColumn("Address");
        model.addColumn("Created At");

        loadCustomers();

        //added the table inside a scrollable panel(vertical)
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        //All buttons
        //creating a main top container to separates left and right buttons
        JPanel topContainer = new JPanel(new BorderLayout());

        //Everything on the left side
        JPanel leftPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
        //find Customer by id
        leftPanel.add(new JLabel("Find By Customer ID:"));
        idField = new JTextField(8);
        leftPanel.add(idField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> findCustomerById() );
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadCustomers();
        });
        leftPanel.add(showAllButton);

        //back button--> go back to dashboard(need to fix position)
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
        deleteButton.addActionListener(e -> deleteCustomer());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> AddCustomer());

        //edit/update button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> EditCustomer());

        rightPanel.add(addButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        //addind the left and right in the top container
        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        //adding the top container and backbutton in the frame
        add(topContainer, BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);
        setVisible(true);



    }

    // Edit Consumer
    private void EditCustomer() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first.");
            return;
        }

        // Get selected row data
        int consumerId = (int) model.getValueAt(selectedRow, 0);
        String name = model.getValueAt(selectedRow, 1).toString();
        String contact = model.getValueAt(selectedRow, 2).toString();
        String address = model.getValueAt(selectedRow, 3).toString();

        // Create dialog
        JDialog dialog = new JDialog(this, "Edit Customer", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("EDIT CUSTOMER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nameField = new JTextField(name);
        JTextField contactField = new JTextField(contact);
        JTextField addressField = new JTextField(address);

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(updateButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Cancel
        cancelButton.addActionListener(e -> dialog.dispose());

        // Update
        updateButton.addActionListener(e -> {

            String newName = nameField.getText().trim();
            String newContact = contactField.getText().trim();
            String newAddress = addressField.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required!");
                return;
            }

            try {
                Connection connection = DBConnection.getConnection();

                String sql = "UPDATE customers SET name = ?, contact_number = ?, address = ? WHERE customer_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, newName);
                ps.setString(2, newContact);
                ps.setString(3, newAddress);
                ps.setInt(4, consumerId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Customer updated successfully!");

                dialog.dispose();

                // Refresh table
                model.setRowCount(0);
                loadCustomers();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error updating customer.");
            }
        });

        dialog.setVisible(true);
    }
    // Create a customer
    private void AddCustomer() {

        // Create dialog
        JDialog dialog = new JDialog(this, "Add Customer", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("ADD CUSTOMER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact Number:"));
        formPanel.add(contactField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Cancel button
        cancelButton.addActionListener(e -> dialog.dispose());

        // Save button
        saveButton.addActionListener(e -> {

            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required!");
                return;
            }

            try {
                Connection connection = DBConnection.getConnection();

                String sql = "INSERT INTO customers (name, contact_number, address) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, name);
                ps.setString(2, contact);
                ps.setString(3, address);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Customer added successfully!");

                dialog.dispose();

                // Refresh table
                model.setRowCount(0);
                loadCustomers();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding customer.");
            }
        });

        dialog.setVisible(true);
    }

    //find a customer by its ID
    private void findCustomerById() {
        String input = idField.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a Customer ID");
            return;
        }

        try {
            //when passing data from frame, the data is ALWAYS in String, so have to do a string conversion, from string to int
            int id = Integer.parseInt(input);

            Connection connection = DBConnection.getConnection();

            String sql = "SELECT * FROM customers WHERE customer_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            // clear table first
            model.setRowCount(0);

            if (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        rs.getTimestamp("created_at")
                });
            } else {
                JOptionPane.showMessageDialog(this, "Customer not found");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a number");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Delete a user
    private void deleteCustomer() {

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
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {
                Connection connection = DBConnection.getConnection();

                String sql = "DELETE FROM customers WHERE customer_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, userId);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");

                // Remove row from table
                model.removeRow(selectedRow);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting customer.");
            }
        }
    }

    //load the table
    private void loadCustomers() {
        try {
            Connection connection = DBConnection.getConnection();


            String sql = "SELECT * FROM customers";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("contact_number"),
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
        new Customer();
    }

    }
