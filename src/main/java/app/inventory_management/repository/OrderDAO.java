package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class OrderDAO {

    Connection connection = DBConnection.getConnection();

    public void insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders(product_id, supplier_id, quantity, price, total_price) VALUES(?,?,?,?,?)";
            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, order.getProductId());
            stmt.setInt(2, order.getSupplierId());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getPrice());
            stmt.setDouble(5, order.getTotalPrice());

            stmt.executeUpdate();
        }

    public Order findOrderById(int id) throws SQLException {

        String sql = "SELECT * FROM orders WHERE order_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Order(
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("supplier_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("created_at")
            );
        } else {
            System.out.println("Order not found");
        }

        return null;
    }

    public List<Order> loadOrders() throws SQLException {

        List<Order> orderList = new ArrayList<>();

        String sql = "SELECT * FROM orders";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getInt("product_id"),
                    rs.getInt("supplier_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("created_at")
            );

            orderList.add(order);
        }

        return orderList;
    }


}


