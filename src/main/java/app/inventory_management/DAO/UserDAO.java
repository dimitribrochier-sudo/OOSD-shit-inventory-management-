package app.inventory_management.DAO;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    Connection connection = DBConnection.getConnection();

    public User findUserById(int id) throws  SQLException{

        String sql = "SELECT user_id, username, full_name, password_hash, role_id, created_at FROM users WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("password_hash"),
                    rs.getInt("role_id"),
                    rs.getTimestamp("created_at")
            );
        }else {
            System.out.println("Error");
        }
        return null;
    }

    public void addUser(User user) throws SQLException{
        String sql = "INSERT INTO users (username, password_hash, full_name, role_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        ps.setInt(4, user.getRoleId());

        ps.executeUpdate();
    }

    public User editUser (User user) throws SQLException{
        String sql = "UPDATE users SET username = ?, password_hash = ?, full_name = ?, role_id = ? WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFullName());
        ps.setInt(4, user.getRoleId());
        ps.setInt(5, user.getUserID());

        //using the row affected response from database as confirmation
        int rows = ps.executeUpdate();
        if(rows > 0){
            return user;
        }else {
            return null;
        }
    }

    public void deleteUser(int id) throws  SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();
    }

    public List<User> loadUsers() throws SQLException {

        List<User> userList = new ArrayList<>();

        String sql = "SELECT * FROM users";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            User user =  new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("password_hash"),
                    rs.getInt("role_id"),
                    rs.getTimestamp("created_at")
            );

            userList.add(user);
        }

        return  userList;
    }

}
