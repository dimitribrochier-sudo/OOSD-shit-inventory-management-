package org.example;

import org.example.config.DBConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.sql.*;

public class Login implements ActionListener {

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

    public Login() {

        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(null);
        frame.setResizable(false);

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

        loginButton.addActionListener(this);
        frame.getRootPane().setDefaultButton(loginButton);

        showPassword.addActionListener(this);

        forgotPasswordButton.addActionListener(this);

        resetButton.addActionListener(this);


        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginButton) {
            try {

                String username = userTextField.getText();
                String password_hash = passwordField.getText();

                Connection connection = DBConnection.getConnection();

                PreparedStatement ps = connection.prepareStatement(
                        "SELECT role_id FROM users WHERE username=? AND password_hash=?"
                );

                ps.setString(1, username);
                ps.setString(2, password_hash);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int roleId = rs.getInt("role_id"); // get role_id
                    JOptionPane.showMessageDialog(null, "Login Successful");

                    frame.dispose();
                    new Dashboard(roleId); // pass role_id to dashboard
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials");
                }

                rs.close();
                ps.close();

                //while switching frames the connection cuts
                //connection.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            }

        //show password--need to review and explain code well
        if (e.getSource() == showPassword) {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('●');
            }

            //code for forget password --> still thibking on what kind of method to implement
            //code for reset page
        }
    }
}