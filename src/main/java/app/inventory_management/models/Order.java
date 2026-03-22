package app.inventory_management.models;

import java.sql.Timestamp;

public class Order {

    private int orderId;
    private int productId;
    private int supplierId;
    private int quantity;
    private double price;
    private double totalPrice;
    private Timestamp created_at;

    //adding from database
    public Order(int productId, int supplierId, int quantity, double price) {
        this.productId = productId;
        this.supplierId = supplierId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = quantity * price;
    }

    //reading from database
    public Order(int orderId, int productId, int supplierId, int quantity, double price, double totalPrice, Timestamp created_at){
        this.orderId = orderId;
        this.productId = productId;
        this.supplierId = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.created_at = created_at;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getProductId() {
        return productId;
    }

    public int getSupplierId() {
        return supplierId;
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
