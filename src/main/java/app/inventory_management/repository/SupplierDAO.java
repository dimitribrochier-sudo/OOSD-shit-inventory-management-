package app.inventory_management.repository;

import java.sql.*;
import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Supplier;

public class SupplierDAO {

    public Supplier getSupplierById(int id) throws SQLException {
        Connection connection = DBConnection.getConnection();

        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            );
        }

        return null;
    }
}
