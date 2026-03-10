package app.inventory_management.controllers;

import app.inventory_management.models.Product;
import app.inventory_management.repository.ProductDAO;

import java.sql.SQLException;
import java.util.List;

public class ProductController {
    ProductDAO productDAO = new ProductDAO();

    public Product findProduct(int id) {

        try{
            return productDAO.findProductById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> loadProduct(){

        try{
            return productDAO.loadProducts();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteProduct(int id) {
        try{
            productDAO.deleteProduct(id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Product product){
        try{
            productDAO.addProduct(product);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editProduct(Product product){
        try{
            productDAO.editProduct(product);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
