package app.inventory_management.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import app.inventory_management.controllers.CustomerController;
import app.inventory_management.models.User;
import app.inventory_management.views.DashboardScreen;
import app.inventory_management.models.Customer;
import java.util.List;
import java.awt.*;


public class CustomerScreen extends JFrame {
    //same user
    private User user;

    //creating a productController
    CustomerController controller = new CustomerController();

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    //SETTING FRAME
    public CustomerScreen() {
        setTitle("CustomerScreen");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        //SETTING THE TABLE
        model = new DefaultTableModel();
        table = new JTable(model);

        //Adding Columns names
        model.addColumn("CustomerScreen ID");
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
        //find CustomerScreen by id
        leftPanel.add(new JLabel("Find By CustomerScreen ID:"));
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
            new DashboardScreen(user);
        });

        //creating the right container
        JPanel rightPanel=new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteCustomer());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addCustomer());

        //edit/update button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editCustomer());

        rightPanel.add(addButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        //addind the left and right in the top container
        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        //adding the top container and backbutton in the frame
        add(topContainer, BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);

    }

    //functions
    private void editCustomer(){
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select customer first.");
            return;
        }

        // Get selected row data
        int customerId = (int) model.getValueAt(selectedRow, 0);
        String name = model.getValueAt(selectedRow, 1).toString();
        String contact = model.getValueAt(selectedRow, 2).toString();
        String address = model.getValueAt(selectedRow, 3).toString();

        // Create dialog
        JDialog dialog = new JDialog(this, "Edit CustomerScreen", true);
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

        //update
        updateButton.addActionListener(e -> {
            String newName = nameField.getText().trim();
            String newContact = contactField.getText().trim();
            String newAddress = addressField.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required!");
                return;
            }

            Customer editedCustomer = new Customer(
                    customerId,
                    newName,
                    Integer.parseInt(newContact),
                    newAddress);

            controller.addCustomer(editedCustomer);
            JOptionPane.showMessageDialog(dialog, "Product updated successfully!");
            dialog.dispose();

            //refresh table
            model.setRowCount(0);
            loadCustomers();
        });
    }

    private void addCustomer(){
        // Create dialog
        JDialog dialog = new JDialog(this, "Add CustomerScreen", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setVisible(true);

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

            Customer newCustomer = new Customer(name, Integer.parseInt(contact), address);

            controller.addCustomer(newCustomer);

            JOptionPane.showMessageDialog(dialog, "CustomerScreen added successfully!");
            dialog.dispose();

            // Refresh table
            model.setRowCount(0);
            loadCustomers();
        });
    }

    private void findCustomerById(){
        String input = idField.getText().trim();

        if (input.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter a customer ID");
            return;
        }

        Customer foundCustomer = controller.findCustomer(Integer.parseInt(input));

        model.setRowCount(0);

        if (foundCustomer != null){
            model.addRow(new Object[]{
                    foundCustomer.getCustomer_id(),
                    foundCustomer.getName(),
                    foundCustomer.getContactNumber(),
                    foundCustomer.getAddress(),
                    foundCustomer.getTimestamp()
            });
        }else {
            JOptionPane.showMessageDialog(this, "Product not found");
        }
    }

    private void deleteCustomer(){

        int selectedRow = table.getSelectedRow();

        //Check if a row is selected
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
            controller.deleteCustomer(selectedRow);
            model.removeRow(selectedRow);

            JOptionPane.showMessageDialog(this, "Customer deleted");
        }
    }

    //loadtable
    private void loadCustomers(){
        //populating data
        List<Customer> customerList = controller.loadCustomer();
        for (Customer c : customerList) {
            model.addRow(new Object[]{
                    c.getCustomer_id(),
                    c.getName(),
                    c.getContactNumber(),
                    c.getAddress(),
                    c.getTimestamp()
            });
        }
    }
    public static void main(String[] args){
        new CustomerScreen();
    }


}
