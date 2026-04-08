package app.inventory_management.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import app.inventory_management.controllers.SupplierController;
import app.inventory_management.models.Supplier;
import app.inventory_management.models.User;
import java.util.List;

public class SupplierScreen extends JFrame {
    //same user
    private User user;

    //creating a SupplierControllerObject
    SupplierController controller = new SupplierController();

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    public SupplierScreen(){


        setTitle("Suppliers");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("Supplier ID");
        model.addColumn("Name");
        model.addColumn("Contact NUmber");
        model.addColumn("Email");
        model.addColumn("Address");
        model.addColumn("Created At");

        //Populating with data
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

        //int searchfield
        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> {
            //clean the input to a single string then boom
            String input = idField.getText().trim();
            try {
                //convert to int
                int id = Integer.parseInt(input);
                //function
                findSupplierById(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SupplierScreen.this, "Please enter a valid numeric ID");
            }
        });

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
            new DashboardScreen();
        });

        //creating the right container
        JPanel rightPanel= new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSupplier());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e ->  addSupplier()); //neeed an object teehee!

        //edit/update button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e ->  editSupplier());

        rightPanel.add(addButton);
        rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        //adding the left and right in the top container
        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        //adding the top container and back button in the frame
        add(topContainer, BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);

        //VISIBLE
        setVisible(true);
    }

    private void addSupplier(){
        // Create dialog
        JDialog dialog = new JDialog(this, "Add Supplier", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("ADD SUPPLIER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(addButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Cancel button
        cancelButton.addActionListener(e -> dialog.dispose());

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phonetext = phoneField.getText().trim();
            String emailtext = emailField.getText().trim();
            String address = addressField.getText().trim();

            //need the phone number check
            int phone;
            String email;

            if (name.isEmpty() || phonetext.isEmpty() ||
                    emailtext.isEmpty() || address.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }



            Supplier supplier = new Supplier(name,phone,email,address);
            //check for null
            if(controller.addSupplier(supplier) != null){
                JOptionPane.showMessageDialog(dialog, "Supplier added successfully!");
                dialog.dispose();
            }
            else {
                JOptionPane.showMessageDialog(dialog, "Supplier Could not be added");
                dialog.dispose();
            }

            //refresh table
            model.setRowCount(0);
            loadSuppliers();
        });

        //visible
        dialog.setVisible(true);
    }

    private void editSupplier() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a Supplier first.");
            return;
        }

        //get selected row data
        int supplierId = (int) model.getValueAt(selectedRow, 0);
        String name = model.getValueAt(selectedRow, 1).toString();
        String phone = model.getValueAt(selectedRow, 2).toString();
        String email = model.getValueAt(selectedRow, 3).toString();
        String address = model.getValueAt(selectedRow, 4).toString();

        // Create dialog
        JDialog dialog = new JDialog(this, "Edit Supplier", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("EDIT SUPPLIER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField nameField = new JTextField(name);
        JTextField phoneField = new JTextField(phone);
        JTextField emailField = new JTextField(email);
        JTextField addressField = new JTextField(address);

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(updateButton);
        formPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Cancel button
        cancelButton.addActionListener(e -> dialog.dispose());

        // Update button
        updateButton.addActionListener(e -> {

            String newName = nameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newAddress = addressField.getText().trim();

            if (newName.isEmpty() || newPhone.isEmpty() ||
                    newEmail.isEmpty() || newAddress.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            Supplier editedSupplier = new Supplier(
                    supplierId,
                    newName,
                    newPhone,
                    newEmail,
                    newAddress
            );

            controller.editSupplier(editedSupplier);
            JOptionPane.showMessageDialog(dialog, "Supplier updated successfully!");
            dialog.dispose();

            //refresh table
            model.setRowCount(0);
            loadSuppliers();
        });
        //dialogvisible
        dialog.setVisible(true);
    }

    //loadtable
    private void loadSuppliers(){
        //populating data
        List<Supplier> supplierList = controller.loadSupplier();
        for (Supplier s : supplierList){
            model.addRow(new Object[]{
                    s.getSupplier_id(),
                    s.getName(),
                    s.getContactNumber(),
                    s.getEmail(),
                    s.getAddress(),
                    s.getTimeStamp()
            });
        }
    }

    private void deleteSupplier(){
        int selectedRow = table.getSelectedRow();

        //check if selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
            return;
        }

        int supplierId = (int) model.getValueAt(selectedRow,0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this Supplier",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteSupplier(supplierId);
            JOptionPane.showMessageDialog(this, "Supplier deleted successfully!");

            // Remove row from table
            model.removeRow(selectedRow);
        }
    }

    private void findSupplierById(int id){

        Supplier found = controller.findSupplier(id);

        model.setRowCount(0);//clear table
        if (found != null) {
            model.addRow(new Object[]{
                    found.getSupplier_id(),
                    found.getName(),
                    found.getContactNumber(),
                    found.getEmail(),
                    found.getAddress(),
                    found.getTimeStamp()
            });
        } else {
            JOptionPane.showMessageDialog(SupplierScreen.this, "Supplier not found!");
        }

    }


}
