package app.inventory_management.views;

import app.inventory_management.models.User;
import app.inventory_management.controllers.UserController;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserScreen extends  JFrame{

    //creating a usercontroller
    UserController controller = new UserController();

    JTable table;
    DefaultTableModel model;
    JTextField idField;

    //SETTING FRAME
    public UserScreen() {
        setTitle("Users");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

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
        //creating a main top container to separates left and right buttons
        JPanel topContainer = new JPanel(new BorderLayout());

        //Everything on the left side
        JPanel leftPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
        //find user by id
        leftPanel.add(new JLabel("Find By User ID:"));
        idField = new JTextField(8);
        leftPanel.add(idField);

        JButton findButton = new JButton("Find");
        findButton.addActionListener(e -> findUserById());
        leftPanel.add(findButton);

        //show all-->a way to revert back after searching as the screen stays on the search result
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> {
            model.setRowCount(0);
            loadUsers();
        });
        leftPanel.add(showAllButton);

        //back button--> go back to dashboard(need to fix position)
        JButton backButton;
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new DashboardScreen();
        });


        //creating the right container
        JPanel rightPanel=new JPanel((new FlowLayout(FlowLayout.RIGHT)));

        //deleting a row
        //tried to put a bin icon but it does not seem to fit in correctly
         /* ImageIcon binIcon= new ImageIcon("C:\\Users\\i\\Documents\\Inventory_Management\\src\\main\\java\\org\\example\\resoures\\images\\bin-svgrepo-com.png");
        Image img = binIcon.getImage();
        Image scaledImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        binIcon = new ImageIcon(scaledImg); */

        //delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteUser());

        //Add/Create Button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> addUser());

        //edit/update button
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> editUser());

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
    //ALL FUNCTIONS

    private void addUser() {
        //create dialog
        JDialog dialog = new JDialog(this, "Add User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setVisible(true);

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

            User newUser = new User(username, password, fullName, Integer.parseInt(role));

            controller.addUser(newUser);

            JOptionPane.showMessageDialog(dialog, "User added successfully!");

            dialog.dispose();

            // Refresh table
            model.setRowCount(0);
            loadUsers();
        });
    }

    private void findUserById(){
        String input = idField.getText().trim();

        if (input.isEmpty()){
            JOptionPane.showMessageDialog(this, "Enter a User ID");
            return;
        }

        User foundUser = controller.findUser(Integer.parseInt(input));

        model.setRowCount(0);

        if (foundUser != null){
            model.addRow(new Object[]{
                    foundUser.getUserID(),
                    foundUser.getUsername(),
                    foundUser.getPassword(),
                    foundUser.getFullName(),
                    foundUser.getRoleId(),
                    foundUser.getCreatedAt()
            });
        }else {
            JOptionPane.showMessageDialog(this, "User not found");
        }
    }

    private void deleteUser(){

        int selectedRow = table.getSelectedRow();

        //checl of rpw is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
            return;
        }
        //Get user_id from column 0
        int userId = (int) model.getValueAt(selectedRow, 0);

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteUser(selectedRow);
            model.removeRow(selectedRow);

            JOptionPane.showMessageDialog(this, "User deleted");
        }
    }

    //loadTable
    private void loadUsers(){
        //populating data
        List<User> userList = controller.loadUser();
        for (User u : userList){
            model.addRow(new Object[]{
                    u.getUserID(),
                    u.getUsername(),
                    u.getPassword(),
                    u.getFullName(),
                    u.getRoleId(),
                    u.getCreatedAt()
            });
        }
    }

    private void editUser() {


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

            User editedUser = new User(
                    userId,
                    newUsername,
                    newPassword,
                    newFullName,
                    Integer.parseInt(roleText)
            );

            controller.editUser(editedUser);
            JOptionPane.showMessageDialog(dialog, "User updated successfully!");
            dialog.dispose();

            //refresh table
            model.setRowCount(0);
            loadUsers();

        });
    }
}
