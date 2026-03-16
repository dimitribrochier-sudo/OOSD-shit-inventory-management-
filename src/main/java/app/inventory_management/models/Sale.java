package app.inventory_management.models;

public class Sale {
    private int productId;
    private int customerId;
    private int quantity;
    private double price;
    private double totalPrice;


    public Sale(int productId, int customerId, int quantity, double price) {
        this.productId = productId;
        this.customerId = customerId;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = quantity * price;
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

}
