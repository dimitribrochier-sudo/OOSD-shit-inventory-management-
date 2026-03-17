package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Sale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaleDAO {

    public void insertSale(Sale sale) throws SQLException {
        String sql = "INSERT INTO sales(product_id, customer_id, quantity, price, total_price) VALUES(?,?,?,?,?)";
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, sale.getProductId());
            stmt.setInt(2, sale.getCustomerId());
            stmt.setInt(3, sale.getQuantity());
            stmt.setDouble(4, sale.getPrice());
            stmt.setDouble(5, sale.getTotalPrice());

            stmt.executeUpdate();
    }
}
