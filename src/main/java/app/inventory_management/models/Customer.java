package app.inventory_management.models;

import java.sql.Timestamp;

public class Customer {
    int customer_id;
    String name;
    int contactNumber;
    String address;
    Timestamp timestamp;

    //this one is used to add it to the database
    public Customer(String name, int contactNumber, String address){
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }
    //this one is used to get it from database
    public Customer(int customer_id, String name, int contactNumber, String address, Timestamp timestamp){
        this.customer_id = customer_id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
        this.timestamp = timestamp;
    }
    //this one is for editing in the database, need it because of timestamp
    public Customer(int customer_id, String name, int contactNumber, String address){
        this.customer_id = customer_id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    //getters
    public int getCustomer_id() {
        return customer_id;
    }
    public String getName() {
        return name;
    }
    public int getContactNumber() {
        return contactNumber;
    }
    public String getAddress() {
        return address;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }

    //setters
    public void setName(String name){
        this.name = name;
    }
    public void setContactNumber(int contactNumber){
        this.contactNumber = contactNumber;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}
