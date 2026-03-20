package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class OrderDAO {

    public void insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders(product_id, supplier_id, quantity, price, total_price) VALUES(?,?,?,?,?)";

            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, order.getProductId());
            stmt.setInt(2, order.getSupplierId());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getPrice());
            stmt.setDouble(5, order.getTotalPrice());

            stmt.executeUpdate();
        }
}


