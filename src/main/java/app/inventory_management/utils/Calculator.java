package app.inventory_management.utils;

public class Calculator {
    //runs most calculation

    public double updateTotal(int quantity, double price){
        try{
            return quantity * price;
        }catch (NumberFormatException e){
            return 0.0;
        }
    }

}
