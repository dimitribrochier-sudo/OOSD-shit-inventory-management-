package org.example.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://127.0.0.1:3306/inventory_management",
                        "root",
                        "password"
                );



            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}



