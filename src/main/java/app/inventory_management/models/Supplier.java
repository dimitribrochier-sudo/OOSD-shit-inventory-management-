package app.inventory_management.models;

import java.sql.Time;
import java.sql.Timestamp;

public class Supplier {
    int supplier_id;
    String name;
    String contactNumber;
    String email;
    String address;
    Timestamp timeStamp;

    //this one is used to add it to the database
    public Supplier(String name, String contactNumber, String email, String address) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
    }

    //this one is to get from the database
    public Supplier(int supplier_id, String name, String contactNumber, String email, String address, Timestamp timeStamp) {
        this.supplier_id = supplier_id;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.timeStamp = timeStamp;
    }

    //getters
    public int getSupplier_id() {
        return supplier_id;
    }
    public String getName() {
        return name;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public String getEmail() {
        return email;
    }
    public String getAddress() {
        return address;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    //setters
    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }
}
