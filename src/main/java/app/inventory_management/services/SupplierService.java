package app.inventory_management.services;

import app.inventory_management.repository.SupplierDAO;
import app.inventory_management.models.Supplier;


public class SupplierService {

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
