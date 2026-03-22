package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Sale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    Connection connection = DBConnection.getConnection();

    public void insertSale(Sale sale) throws SQLException {
        String sql = "INSERT INTO sales(productId, customerId, quantity, price, totalPrice) VALUES(?,?,?,?,?)";

            PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setInt(1, sale.getProductId());
            stmt.setInt(2, sale.getCustomerId());
            stmt.setInt(3, sale.getQuantity());
            stmt.setDouble(4, sale.getPrice());
            stmt.setDouble(5, sale.getTotalPrice());

            stmt.executeUpdate();
    }

    public Sale findSaleById(int id) throws SQLException {

        String sql = "SELECT * FROM sales WHERE id = ? ";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Sale(
                    rs.getInt("id"),
                    rs.getInt("productId"),
                    rs.getInt("customerId"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("totalPrice"),
                    rs.getTimestamp("created_at")
            );
        } else {
            System.out.println("Error? idk Something went bad");
        }
        return null;

    }

    public List<Sale> loadSales() throws SQLException {

        List<Sale> saleList = new ArrayList<>();

        String sql = "SELECT * FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            Sale sale = new Sale(
                    rs.getInt("id"),
                    rs.getInt("productId"),
                    rs.getInt("customerId"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("totalPrice"),
                    rs.getTimestamp("created_at")
            );

            saleList.add(sale);
        }
        return saleList;
    }
}
