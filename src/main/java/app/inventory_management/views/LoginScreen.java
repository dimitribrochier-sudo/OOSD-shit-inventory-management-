package app.inventory_management.views;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginScreen  extends JFrame{

    //frame title(appears on top left)
    JFrame frame = new JFrame("LOGIN");

    //welcome text
    JLabel titleLabel = new JLabel("WELCOME");

    JLabel userLabel = new JLabel("USERNAME");
    JTextField userTextField = new JTextField();

    JLabel passwordLabel = new JLabel("PASSWORD");
    JPasswordField passwordField = new JPasswordField();

    JButton loginButton = new JButton("LOGIN");

    JButton forgotPasswordButton = new JButton("FORGOT PASSWORD");

    JCheckBox showPassword = new JCheckBox("Show Password");

    JButton resetButton = new JButton("Reset");

    public LoginScreen() {

        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);
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


        forgotPasswordButton.setBounds(100, 340, 180, 35);
        forgotPasswordButton.setFont(new Font("Arial", Font.BOLD, 13));
        forgotPasswordButton.setFocusable(false);

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
        frame.add(forgotPasswordButton);
        frame.add(resetButton);


    }

}
