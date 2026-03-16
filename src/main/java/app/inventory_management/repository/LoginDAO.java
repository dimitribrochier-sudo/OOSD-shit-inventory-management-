package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.User;
import java.sql.*;

public class LoginDAO {
    Connection connection = DBConnection.getConnection();

    public User login(String username, String password_hash) throws SQLException{

            String sql = "SELECT * FROM users WHERE username=? AND password_hash=?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password_hash);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getInt("role_id"),
                        rs.getTimestamp("created_at"));
            }
            return  null;
        }
}
