package app.inventory_management.controllers;

import app.inventory_management.models.User;
import app.inventory_management.DAO.UserDAO;

import java.sql.SQLException;
import java.util.List;

public class UserController {
    UserDAO userDAO = new UserDAO();

    public User findUser(int id){
        try {
            return userDAO.findUserById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> loadUser(){
        try{
            return userDAO.loadUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUser(User user){
        try{
            userDAO.addUser(user);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void editUser(User user){
        try{
            userDAO.editUser(user);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteUser(int id) {
        try{
            userDAO.deleteUser(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
