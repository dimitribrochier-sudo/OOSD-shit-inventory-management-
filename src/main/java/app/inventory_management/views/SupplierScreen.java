package app.inventory_management.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import app.inventory_management.controllers.SupplierController;
import app.inventory_management.models.Supplier;
import app.inventory_management.models.User;


public class SupplierScreen extends JFrame {
    //same user
    private User user;

    //creating a SupplierServiceObject
    SupplierController controller = new SupplierController();

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    public SupplierScreen(User user){
        //user
        this.user = user;

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

        //Populating with data
        controller.loadSupplier();

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
        //clean the input to a single string then boom
        String input = idField.getText().trim();
        //convert to int.
        findButton.addActionListener(e -> controller.findSupplier(Integer.parseInt(input)));
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            controller.loadSupplier();
        });
        leftPanel.add(showAllButton);

        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new DashboardScreen(user);
        });

        //creating the right container
        JPanel rightPanel= new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> controller.deleteSupplier(selectedRow()));

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e ->  addSupplierWidget()); //neeed an object teehee!

        //edit/update button
        //JButton editButton = new JButton("Edit");
        //editButton.addActionListener(e ->  EditSupplier());


        rightPanel.add(addButton);
        //rightPanel.add(editButton);
        rightPanel.add(deleteButton);

        //adding the left and right in the top container
        topContainer.add(leftPanel, BorderLayout.WEST);
        topContainer.add(rightPanel, BorderLayout.EAST);

        //adding the top container and back button in the frame
        add(topContainer, BorderLayout.NORTH);
        add(backButton,BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addSupplierWidget(){
        // Create dialog
        JDialog dialog = new JDialog(this, "Add Supplier", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setVisible(true);

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
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() ||
                    email.isEmpty() || address.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            Supplier supplier = new Supplier(name,phone,email,address);
            controller.addSupplier(supplier);

        });

    }

    //having a selecting function
    public int selectedRow (){
        return table.getSelectedRow();
    }

}
