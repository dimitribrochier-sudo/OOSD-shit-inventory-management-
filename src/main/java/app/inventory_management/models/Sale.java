package app.inventory_management.models;

import java.sql.Timestamp;

public class Sale {
    private int saleId;
    private int productId;
    private int customerId;
    private int quantity;
    private double price;
    private double totalPrice;
    private Timestamp created_at;

    //adding a sale
    public Sale(int productId, int customerId, int quantity, double price) {
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = quantity * price;
    }

    //loading a sale
    public Sale(int saleId, int productId, int customerId, int quantity, double price, double totalPrice, Timestamp created_at) {
        this.saleId = saleId;
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = quantity * price;
        this.created_at = created_at;
    }

    public int getSaleId() {
        return saleId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getProductId() {
        return productId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }



}
