package app.inventory_management.controllers;

import app.inventory_management.models.Order;
import app.inventory_management.models.Product;
import app.inventory_management.DAO.OrderDAO;
import java.sql.SQLException;
import java.util.List;


public class OrderController {

    OrderDAO orderDAO = new OrderDAO();
    ProductController controller = new ProductController();

    public void createOrder(int productId, int supplierId, int quantity, double price){
        try{
            Product product = controller.findProduct(productId);

            if (product == null) {
                throw new RuntimeException("Product not Found!");
            }

            if (quantity <= 0) {
                throw new RuntimeException("Quantity must be greater than 0!");
            }

            //increasing the inventorystock
            int newStock = product.getCurrentStock() + quantity;
            product.setCurrentStock(newStock);

            controller.editProduct(product);

            //creating and saving order
            Order order = new Order(productId, supplierId, quantity, price);
            orderDAO.insertOrder(order);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Order> loadOrders() {
        try{
            return orderDAO.loadOrders();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Order findOrder(int id) {
        try {
            return orderDAO.findOrderById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
