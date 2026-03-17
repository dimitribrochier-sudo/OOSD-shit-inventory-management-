package app.inventory_management.controllers;

import app.inventory_management.models.Supplier;
import app.inventory_management.repository.SupplierDAO;

import java.sql.SQLException;
import java.util.List;


public class SupplierController {
    SupplierDAO supplierDAO = new SupplierDAO();

    public Supplier findSupplier(int id) {
        try {
            return supplierDAO.getSupplierById(id);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<Supplier> loadSupplier() {
        try {
            return supplierDAO.loadSuppliers();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void deleteSupplier(int id) {
        try {
            supplierDAO.deleteSupplier(id);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void addSupplier(Supplier supplier) {
        try {
            supplierDAO.addSupplier(supplier);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void editSupplier(Supplier supplier){
        try{
            supplierDAO.editSupplier(supplier);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
