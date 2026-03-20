package app.inventory_management.controllers;

import app.inventory_management.models.Order;
import app.inventory_management.repository.OrderDAO;

import java.sql.SQLException;


public class OrderController {
    OrderDAO dao = new OrderDAO();

    public void createOrder(int productId, int supplierId, int quantity, double price){
        try{
            Order order = new Order(productId, supplierId, quantity, price);

            dao.insertOrder(order);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
