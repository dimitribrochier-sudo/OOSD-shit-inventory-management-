package app.inventory_management.repository;

import java.sql.*;
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
                    rs.getString("contact_number"),
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

    public void addSupplier(Supplier supplier) throws SQLException{
        String sql = "INSERT INTO suppliers (name, contact_number, email, address) VALUES(?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setString(1, supplier.getName());
        ps.setString(2, supplier.getContactNumber());
        ps.setString(3, supplier.getEmail());
        ps.setString(4, supplier.getAddress());

    }

    public void deleteSupplier(int id) throws SQLException {

        try {
            String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public Supplier loadSuppliers() throws SQLException{
        String sql = "SELECT * FROM Suppliers";
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            return new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getTimestamp("created_at")
            );
        }
        return null;
    }

    //work in progress
    public void editSupplier(int id) {

    }

}
