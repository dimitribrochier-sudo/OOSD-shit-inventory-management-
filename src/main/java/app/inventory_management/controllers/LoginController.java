package app.inventory_management.controllers;

import app.inventory_management.models.User;
import app.inventory_management.repository.LoginDAO;

public class LoginController {
    LoginDAO loginDAO = new LoginDAO();

    public User loginUser(String username, String password) {

        try {
            return loginDAO.login(username, password);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
