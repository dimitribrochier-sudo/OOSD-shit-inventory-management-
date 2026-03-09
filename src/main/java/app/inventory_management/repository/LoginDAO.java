package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.User;
import java.sql.*;

public class LoginDAO {
    Connection connection = DBConnection.getConnection();

    public User login(String username, String password_hash) throws SQLException{

        try {
            String sql = "SELECT role_id FROM users WHERE username=? AND password_hash=?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password_hash);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int roleId = rs.getInt("role_id");

                return new User(username, roleId);

            }
            return  null;

        }catch (SQLException e){
            throw new SQLException(e);
        }


    }
}
