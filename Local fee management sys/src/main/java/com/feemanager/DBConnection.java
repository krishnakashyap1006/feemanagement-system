package com.feemanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection class
 * 
 * this class is used to establish a connection with the MySQL database.
 * entire application will use this class to get the connection object.
 */
public class DBConnection {

    // MySQL server's address
    private static final String URL = "jdbc:mysql://localhost:3306/fee_management";

    // MySQL username
    private static final String USER = "root";

    // write your mysql password here
    private static final String PASSWORD = "1234";

    /**
     * Returns a Connection object to the MySQL database.
     */
    public static Connection getConnection() {

        try {

            // MySQL database ke saath connection create kar rahe hain
            // Class.forName("com.mysql.cj.jdbc.Driver");
            // System.out.println("Driver Loaded");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connected successfully!");

            return connection;

        } catch (SQLException e) {

            // Agar connection fail ho jaye to error show hoga
            System.out.println("Database connection failed!");
            System.out.println("Error: " + e.getMessage());

            return null;
        }
    }
}