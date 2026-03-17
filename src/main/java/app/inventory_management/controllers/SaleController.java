package app.inventory_management.controllers;

import app.inventory_management.models.Sale;
import app.inventory_management.repository.SaleDAO;

import java.sql.SQLException;


public class SaleController {
    SaleDAO dao = new SaleDAO();

    public void createSale(int productId, int customerId, int quantity, double price) {
    try{
        Sale sale = new Sale(productId, customerId, quantity, price);

        dao.insertSale(sale);
        }

        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
