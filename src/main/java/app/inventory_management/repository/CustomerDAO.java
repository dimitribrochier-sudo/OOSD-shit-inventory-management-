package app.inventory_management.repository;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAO {
    Connection connection = DBConnection.getConnection();

    public Customer findCustomerById(int id) throws SQLException{

        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            return new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getInt("contact_number"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            );
        }else{
            System.out.println("Error");
        }
        return null;
    }

    public Customer editCustomer (Customer customer) throws SQLException{
        String sql = "UPDATE customers SET name = ?, contact_number = ?, address = ? WHERE customer_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, customer.getName());
        ps.setInt(2, customer.getContactNumber());
        ps.setString(3, customer.getAddress());
        ps.setInt(4, customer.getCustomer_id());

        //using the row affected response from database as confirmation
       int rows = ps.executeUpdate();
       if(rows > 0){
           return customer;
       }else {
           return null;
       }
    }

    public void deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();
    }

    public List<Customer> loadCustomers() throws SQLException {

        List<Customer> customerList = new ArrayList<>();

        String sql = "SELECT * FROM customers";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Customer customer = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getInt("contact_number"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            );

            customerList.add(customer);
        }

        return customerList;
    }

    public void addCustomer(Customer customer) throws SQLException{
        String sql = "INSERT INTO customers (name, contact_number, address) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, customer.getName());
        ps.setInt(2, customer.getContactNumber());
        ps.setString(3, customer.getAddress());

        ps.executeUpdate();

    }


}
