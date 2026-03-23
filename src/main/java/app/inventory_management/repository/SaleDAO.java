package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Sale;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //for total sales analytics
    public int getTotalSales() throws SQLException {
        String sql = "SELECT SUM(totalPrice) FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        return rs.next() ? rs.getInt(1) : 0;
    }

    //for total orders analytics
    public int getTotalOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        return rs.next() ? rs.getInt(1) : 0;
    }

    //for total quantity analytics
    public int getTotalQuantity() throws SQLException {
        String sql = "SELECT SUM(quantity) FROM sales";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        return rs.next() ? rs.getInt(1) : 0;
    }

    //for top products analytics
    public String getTopProduct() throws SQLException {
        String sql = """
        SELECT productId, SUM(totalPrice) AS total
        FROM sales
        GROUP BY productId
        ORDER BY total DESC
        LIMIT 1
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return "P" + rs.getInt("productId");
        }
        return "N/A";
    }


    //for the barchart we need a hashmap
    public Map<String, Integer> salesPerProduct() throws SQLException {
        //hashmap stores 2 values in a nutshell
        Map<String, Integer> data = new HashMap<>();

        String sql = "SELECT productId, SUM(totalPrice) AS totalSales FROM sales GROUP BY productId";

        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            String product = "p" + rs.getInt("productId");
            int total = rs.getInt("totalSales");

            data.put(product, total);
        }
        return data;
    }




}
