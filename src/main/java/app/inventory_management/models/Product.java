package app.inventory_management.models;

public class Product {
    int product_id;
    String name;
    String[] category = {"Electronics", "Accessories", "Storage"};
    double unitPrice;
    int currentStock;
    int reorderLevel;
}
