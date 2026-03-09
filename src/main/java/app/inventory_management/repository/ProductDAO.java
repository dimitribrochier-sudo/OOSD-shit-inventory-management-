package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductDAO {
    Connection connection = DBConnection.getConnection();

    public Product findProductById(int id) throws SQLException {

            String sql = "SELECT * FROM products WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            //for enum
            Product.Category category =
                    Product.Category.valueOf(rs.getString("category").toUpperCase());

            if(rs.next()){
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        category,
                        rs.getDouble("unit_price"),
                        rs.getInt("current_stock"),
                        rs.getInt("reorder_level"),
                        rs.getInt("supplier_id")
                );
            }else {
                System.out.println("Error? idk Something went bad");
        }
        return null;
    }

    public void editProduct (
            int productId,
            String newName,
            Product.Category newCategory,
            double newUnitPrice,
            int newCurrentStock,
            int newReorderLevel,
            int newSupplierId) throws SQLException
    {
        String sql = "UPDATE products SET name=?, category=?, unit_price=?, current_stock=?, reorder_level=?, supplier_id=? WHERE product_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, newName);
        ps.setString(2, newCategory.toString());
        ps.setDouble(3, newUnitPrice);
        ps.setInt(4, newCurrentStock);
        ps.setInt(5, newReorderLevel);
        ps.setInt(6, newSupplierId);
        ps.setInt(7, productId);
    }

    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();
    }

    public Product loadProducts() throws SQLException {
        String sql = "SELECT * FROM Products";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //for enum
        Product.Category category =
                Product.Category.valueOf(rs.getString("category").toUpperCase());

        while(rs.next()){
            return new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    category,
                    rs.getInt("unit_price"),
                    rs.getInt("current_stock"),
                    rs.getInt("reorder_level"),
                    rs.getInt("supplier_id")
            );
        }
        return null;
    }

    public void addProduct(Product product) throws SQLException{

        String sql = "INSERT INTO products (name, category, unit_price, current_stock, reorder_level, supplier_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);


        ps.setString(1, product.getName());
        ps.setString(2, product.getCategory().toString());
        ps.setDouble(3, product.getUnitPrice());
        ps.setInt(4, product.getCurrentStock());
        ps.setInt(5, product.getReorderLevel());
        ps.setInt(6, product.getSupplier_id());

        ps.executeUpdate();

    }

}
