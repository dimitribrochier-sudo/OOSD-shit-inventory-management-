package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    Connection connection = DBConnection.getConnection();

    public Product findProductById(int id) throws SQLException {

            String sql = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){

                //for enum
                Product.Category category =
                        Product.Category.valueOf(rs.getString("category").toUpperCase());

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

    public Product editProduct (Product product) throws SQLException
    {
        String sql = "UPDATE products SET name=?, category=?, unit_price=?, current_stock=?, reorder_level=?, supplier_id=? WHERE product_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, product.getName());
        ps.setString(2, product.getCategory().toString());
        ps.setDouble(3, product.getUnitPrice());
        ps.setInt(4, product.getCurrentStock());
        ps.setInt(5, product.getReorderLevel());
        ps.setInt(6, product.getSupplier_id());
        ps.setInt(7, product.getProductId());

        //using the row affected response from database as confirmation
        int rows = ps.executeUpdate();
        if(rows > 0) {
            return product;
        } else {
            return null;
        }
    }

    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();
    }

    public List<Product> loadProducts() throws SQLException {

        List<Product> productList = new ArrayList<>();

        String sql = "SELECT * FROM Products";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            //for enum
            Product.Category category =
                    Product.Category.valueOf(rs.getString("category").toUpperCase());

            Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    category,
                    rs.getInt("unit_price"),
                    rs.getInt("current_stock"),
                    rs.getInt("reorder_level"),
                    rs.getInt("supplier_id")
            );

            productList.add(product);
        }
        return productList;
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
