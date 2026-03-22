package app.inventory_management.controllers;

import app.inventory_management.models.Sale;
import app.inventory_management.repository.SaleDAO;
import app.inventory_management.models.Product;
import app.inventory_management.controllers.ProductController;
import java.sql.SQLException;
import java.util.List;

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

}
