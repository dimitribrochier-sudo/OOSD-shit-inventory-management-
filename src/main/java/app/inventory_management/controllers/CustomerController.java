package app.inventory_management.controllers;

import app.inventory_management.models.Customer;
import app.inventory_management.repository.CustomerDAO;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {
    CustomerDAO customerDAO = new CustomerDAO();

    public Customer findCustomer(int id) {
        try{
            return customerDAO.findCustomerById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Customer> loadCustomer(){
        try {
            return customerDAO.loadCustomers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCustomer(int id) {
        try{
            customerDAO.deleteCustomer(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCustomer(Customer customer){
        try{
            customerDAO.addCustomer(customer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void editCustomer(Customer customer){
        try{
            customerDAO.editCustomer(customer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
