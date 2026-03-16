package app.inventory_management.controllers;

import app.inventory_management.models.User;
import app.inventory_management.repository.LoginDAO;

import java.sql.SQLException;

public class LoginController {
    LoginDAO loginDAO = new LoginDAO();

    public User loginUser(String username, String password) {

        try {
            return loginDAO.login(username, password);

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
