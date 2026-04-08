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
        setResizable(false);

        this.getContentPane().setBackground(new Color(15, 23, 42));//bg color
        // Sets the color for the entire background of the window


        //SETTING THE TABLE
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setBackground(new Color(30, 41, 59));
        table.setForeground(new Color(241, 245, 249));
        table.setGridColor(new Color(51, 65, 85));
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(15, 23, 42));
        table.getTableHeader().setForeground(new Color(241, 245, 249));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Adding Columns names
        model.addColumn("User ID");
        model.addColumn("Username");
        model.addColumn("Full Name");
        model.addColumn("Password");
        model.addColumn("Role ID");
        model.addColumn("Created At");

        loadUsers();

        //added the table inside a scrollable panel(vertical)
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.getViewport().setBackground(new Color(15, 23, 42));//chnaged color for section below table
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        //All buttons
        //creating a main top container to separates left and right buttons
        JPanel topContainer = new JPanel(new BorderLayout());

        //Everything on the left side
        JPanel leftPanel = new JPanel((new FlowLayout(FlowLayout.LEFT)));
        leftPanel.setBackground(new Color(15, 23, 42));//chnaged
        leftPanel.setForeground(new Color(241, 245, 249));//chnaged

        //find user by id -chnaged this part
        JLabel findLabel = new JLabel("Find By User ID:");
        findLabel.setForeground(new Color(241, 245, 249));
        leftPanel.add(findLabel);
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
        rightPanel.setBackground(new Color(15, 23, 42));//chnaged

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

        //styling topContainer
        topContainer.setBackground(new Color(15, 23, 42));//chnaged

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

        dialog.getContentPane().setBackground(new Color(15, 23, 42));


        // title add user
        JLabel titleLabel = new JLabel("ADD USER", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(241, 245, 249));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        //creating the form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(new Color(15, 23, 42));
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
            //check for roleId
            if (!role.equals("1") && !role.equals("2")) {
                JOptionPane.showMessageDialog(dialog, "Incorrect RoleId. Use 1 or 2");
                return;
            }
            //end of check
            User newUser = new User(username, password, fullName, Integer.parseInt(role));

            controller.addUser(newUser);

            JOptionPane.showMessageDialog(dialog, "User added successfully!");

            dialog.dispose();

            // Refresh table
            model.setRowCount(0);
            loadUsers();

        });
        for (Component comp : formPanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(new Color(241, 245, 249));
            } else if (comp instanceof JTextField) {
                comp.setBackground(new Color(30, 41, 59));
                comp.setForeground(Color.WHITE);

                ((JTextField) comp).setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
            }
        }

        dialog.setVisible(true);
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
            controller.deleteUser(userId);
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
        String password = model.getValueAt(selectedRow, 2).toString();
        String fullName = model.getValueAt(selectedRow, 3).toString();
        int roleId = (int) model.getValueAt(selectedRow, 4);

        // Create dialog
        JDialog dialog = new JDialog(this, "Edit User", true);
        dialog.getContentPane().setBackground(new Color(15, 23, 42));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        //Ttile edit user
        JLabel titleLabel = new JLabel("EDIT USER", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(241, 245, 249));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        dialog.add(titleLabel, BorderLayout.NORTH);

        //creating the form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBackground(new Color(15, 23, 42));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JTextField usernameField = new JTextField(username);
        JTextField fullNameField = new JTextField(fullName);
        JTextField passwordField = new JTextField(password);
        JTextField roleIdField = new JTextField(String.valueOf(roleId));

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        JLabel userLbl = new JLabel("Username:");
        userLbl.setForeground(new Color(241, 245, 249));
        formPanel.add(userLbl);
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
        for (Component comp : formPanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setForeground(new Color(241, 245, 249));
            } else if (comp instanceof JTextField) {
                comp.setBackground(new Color(30, 41, 59));
                comp.setForeground(Color.WHITE);
                ((JTextField) comp).setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));
            }
        }

        dialog.setVisible(true);
    }
}
