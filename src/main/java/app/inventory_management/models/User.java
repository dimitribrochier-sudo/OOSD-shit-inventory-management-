package app.inventory_management.models;

public class User {
    private String username;
    private int roleId;

    public User(String username, int roleId) {
        this.username = username;
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public int getRoleId() {
        return roleId;
    }
}
