package app.inventory_management.views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import app.inventory_management.repository.SupplierDAO;
import app.inventory_management.models.Supplier;
import app.inventory_management.services.SupplierService;
import app.inventory_management.views.DashboardScreen;



public class SupplierScreen extends JFrame {
    //creating a SupplierServiceObject

    SupplierService service = new SupplierService();

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    public SupplierScreen(){
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
        service.loadSupplier();

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
        findButton.addActionListener(e -> service.findSupplier(Integer.parseInt(input)));
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            service.loadSupplier();
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
        deleteButton.addActionListener(e -> service.deleteSupplier(selectedRow()));

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e ->  service.addSupplier()); //neeed an object teehee!

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
    //having a selecting function
    public int selectedRow (){
        return table.getSelectedRow();
    }

}
