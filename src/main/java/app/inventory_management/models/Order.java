package app.inventory_management.models;

public class Order {

    private int productId;
    private int supplierId;
    private int quantity;
    private double price;
    private double totalPrice;


    public Order(int productId, int supplierId, int quantity, double price) {
        this.productId = productId;
        this.supplierId = supplierId;
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

    public int getSupplierId() {
        return supplierId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

}
