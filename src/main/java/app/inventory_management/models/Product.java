package app.inventory_management.models;

public class Product {
   private int product_id;
   private String name;
   private Category category;
   private double unitPrice;
   private int currentStock;
   private int reorderLevel;
   private int supplier_id;

    public enum Category{
        ELECTRONICS,
        ACCESSORIES,
        STORAGE
    }

    //this one is used to add it to the database
    public Product(String name, Category category, double unitPrice, int currentStock, int reorderLevel){
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
    }
    //this one is used to get it from database
    public Product(int product_id, String name, Category category, double unitPrice, int currentStock, int reorderLevel, int supplier_id){
        this.product_id = product_id;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
        this.supplier_id = supplier_id;
    }

    //getters
    public int getProductId() {
        return product_id;
    }
    public String getName() {
        return name;
    }
    public Category getCategory() {
        return category;
    }
    public double getUnitPrice() {
        return unitPrice;
    }
    public int getCurrentStock() {
        return currentStock;
    }
    public int getReorderLevel() {
        return reorderLevel;
    }
    public int getSupplier_id(){
        return supplier_id;
    }

    //setters
    public void setName(String name) {
        this.name = name;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
}
