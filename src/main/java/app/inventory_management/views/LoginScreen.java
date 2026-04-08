package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import app.inventory_management.controllers.LoginController;
import app.inventory_management.models.User;
import app.inventory_management.utils.SessionManager;


public class LoginScreen implements ActionListener{

    //frame title(appears on top left)
    JFrame frame = new JFrame("LOGIN");

    //welcome text
    JLabel titleLabel = new JLabel("WELCOME");

    JLabel userLabel = new JLabel("USERNAME");
    JTextField userTextField = new JTextField();

    JLabel passwordLabel = new JLabel("PASSWORD");
    JPasswordField passwordField = new JPasswordField();

    JButton loginButton = new JButton("LOGIN");

    //JButton forgotPasswordButton = new JButton("FORGOT PASSWORD");

    JCheckBox showPassword = new JCheckBox("Show Password");

    JButton resetButton = new JButton("Reset");

    public LoginScreen() {

        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getRootPane().setDefaultButton(loginButton);

        userLabel.setBounds(40, 120, 100, 50);
        userLabel.setFont(new Font("Arial", Font.BOLD, 17));

        titleLabel.setBounds(100, 40, 200, 50);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        passwordLabel.setBounds(40, 190, 100, 50);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 17));

        userTextField.setBounds(180, 135, 150, 25);
        userTextField.setFont(new Font("Arial", Font.BOLD, 15));

        passwordField.setBounds(180, 200, 150, 25);
        passwordField.setFont(new Font("Arial", Font.BOLD, 15));

        showPassword.setBounds(180, 230, 150, 25);
        showPassword.setFont(new Font("Arial", Font.BOLD, 15));
        showPassword.setFocusable(false);

        loginButton.setBounds(100, 280, 180, 35);
        loginButton.setFont(new Font("Arial", Font.BOLD, 15));
        loginButton.setFocusable(false);


        //forgotPasswordButton.setBounds(100, 340, 180, 35);
        //forgotPasswordButton.setFont(new Font("Arial", Font.BOLD, 13));
        //forgotPasswordButton.setFocusable(false);

        resetButton.setBounds(100,400,180,35);
        resetButton.setFont(new Font("Arial", Font.BOLD, 15));
        resetButton.setFocusable(false);

        frame.add(titleLabel);
        frame.add(userLabel);
        frame.add(passwordLabel);
        frame.add(userTextField);
        frame.add(passwordField);
        frame.add(showPassword);
        frame.add(loginButton);
        //frame.add(forgotPasswordButton);
        frame.add(resetButton);


        loginButton.addActionListener(this);

        showPassword.addActionListener(this);

        //forgotPasswordButton.addActionListener(this);

        resetButton.addActionListener(this);

        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //input into a user object
        String name = userTextField.getText();
        String password_hash = passwordField.getText();

        LoginController controller = new LoginController();
        User user = controller.loginUser(name, password_hash);

        if (e.getSource() == loginButton) {
            if(user != null){
                JOptionPane.showMessageDialog(null, "Login Successful");
                frame.dispose();
                //store the user
                SessionManager.getInstance().login(user);
                new DashboardScreen();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials");
            }
        }

        //show password--need to review and explain code well
        if (e.getSource() == showPassword) {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        }

        //reset-->clearing all fields
        if (e.getSource() == resetButton) {
            userTextField.setText("");
            passwordField.setText("");
            showPassword.setSelected(false);
            passwordField.setEchoChar('*'); // restore masking
            userTextField.requestFocus();   // cursor back to username
        }

        //code for forget password --> still thibking on what kind of method to implement

        //if (e.getSource() == forgotPasswordButton){
        //    JOptionPane.showMessageDialog(null, "Well Bro, shit hasn't been implemented yet!");

        //}
    }




}
