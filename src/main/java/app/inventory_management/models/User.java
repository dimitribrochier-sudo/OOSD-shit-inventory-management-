package app.inventory_management.models;

import java.sql.Timestamp;

public class User {
    private int userID;
    private String username;
    private String password;
    private String fullName;
    private int roleId;
    private Timestamp createdAt;

//for getting user data from the database
    public User(int userID, String username, String password, String fullName, int roleId, Timestamp createdAt) {
        this.userID= userID;
        this.username = username;
        this.password=password;
        this.fullName=fullName;
        this.roleId = roleId;
        this.createdAt=createdAt;
    }

//for putting user in the database
    public User(String username, String password, String fullName, int roleId){
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.roleId = roleId;
    }

//for editing in database
public User(int userID, String username, String password, String fullName, int roleId){
    this.userID = userID;
    this.username = username;
    this.password = password;
    this.fullName = fullName;
    this.roleId = roleId;
}

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getRoleId() {
        return roleId;
    }
}
