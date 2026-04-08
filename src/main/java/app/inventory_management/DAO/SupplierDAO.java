package app.inventory_management.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import app.inventory_management.config.DBConnection;
import app.inventory_management.models.Supplier;

public class SupplierDAO {

    Connection connection = DBConnection.getConnection();

    public Supplier getSupplierById(int id) throws SQLException {


        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getInt("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            );
        }

        else{
            System.out.println("Error? IDK something went bad");
        }
        return null;
    }

    public Supplier addSupplier(Supplier supplier) throws SQLException{
        String sql = "INSERT INTO suppliers (name, contact_number, email, address) VALUES(?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, supplier.getName());
        ps.setInt(2, supplier.getContactNumber());
        ps.setString(3, supplier.getEmail());
        ps.setString(4, supplier.getAddress());


        //using the row affected as a response from database
        int rows = ps.executeUpdate();
        if(rows > 0){
            return supplier;
        }else{
            return null;
        }
    }

    public void deleteSupplier(int id) throws SQLException {

        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        ps.executeUpdate();
    }


    public List<Supplier> loadSuppliers() throws SQLException{

        List<Supplier> supplierList = new ArrayList<>();

        String sql = "SELECT * FROM Suppliers";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            Supplier supplier = new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getInt("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            );

            supplierList.add(supplier);
        }

        return supplierList;
    }


    public Supplier editSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET name=?,contact_number =?, email=?, address=? WHERE supplier_id=?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, supplier.getName());
        ps.setInt(2, supplier.getContactNumber());
        ps.setString(3, supplier.getEmail());
        ps.setString(4, supplier.getAddress());
        ps.setInt(5, supplier.getSupplier_id());

        ps.executeUpdate();
        //using the row affected response from database as confirmation
        int rows = ps.executeUpdate();
        if(rows > 0){
            return supplier;
        }else {
            return null;
        }
    }

}
