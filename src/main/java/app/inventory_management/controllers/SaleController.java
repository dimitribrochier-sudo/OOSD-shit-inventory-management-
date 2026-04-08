package app.inventory_management.controllers;

import app.inventory_management.models.Sale;
import app.inventory_management.DAO.SaleDAO;
import app.inventory_management.models.Product;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleController {

    ProductController controller = new ProductController();

    SaleDAO saleDAO = new SaleDAO();

    public void createSale(int productId, int customerId, int quantity, double price) {
        try{
            Product product = controller.findProduct(productId);

            if(product == null){
                throw new RuntimeException("Product Not Found!");
            }

            //checkStock
            if (product.getCurrentStock() < quantity) {
                throw new RuntimeException("Not Enough Stock! Current Stock is at: " + product.getCurrentStock());
            }

            //do stock reduction an edit the count in database.
            int newStock = product.getCurrentStock() - quantity;
            product.setCurrentStock(newStock);
            controller.editProduct(product);

            //making teh sale
            Sale sale = new Sale(productId, customerId, quantity, price);
            saleDAO.insertSale(sale);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Sale> loadSale() {
        try{
            return saleDAO.loadSales();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Sale findSale(int id) {
        try {
            return saleDAO.findSaleById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Integer> salesPerProduct() {
        try{
            return saleDAO.salesPerProduct();
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    //for analytics

    public int getTotalSales() {
        try{
            return saleDAO.getTotalSales();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalOrders() {
        try{
            return saleDAO.getTotalOrders();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalQuantity() {
        try{
            return saleDAO.getTotalQuantity();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTopProduct() {
        try{
            return saleDAO.getTopProduct();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
