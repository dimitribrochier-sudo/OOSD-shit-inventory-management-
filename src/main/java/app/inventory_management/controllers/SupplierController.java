package app.inventory_management.controllers;

import app.inventory_management.models.Supplier;
import app.inventory_management.repository.SupplierDAO;


public class SupplierController {
    SupplierDAO supplierDAO = new SupplierDAO();

    public Supplier findSupplier(int id) {

        try {
            return supplierDAO.getSupplierById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Supplier loadSupplier() {
        try {
            return supplierDAO.loadSuppliers();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSupplier(int id) {
        try {
            supplierDAO.deleteSupplier(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSupplier(Supplier supplier) {
        try {
            supplierDAO.addSupplier(supplier);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
