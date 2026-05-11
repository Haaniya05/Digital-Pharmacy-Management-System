package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java - Singleton class for managing PostgreSQL database connections.
 *
 * Make sure postgresql-<version>.jar is added to your project libraries.
 */

public class DBConnection {

    // ==================== Database Configuration ====================
    // Change these values according to your PostgreSQL setup

    private static final String DB_URL =
            "jdbc:postgresql://localhost:5432/pharmacy_db";

    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "haaniya"; 

    // Singleton instance
    private static Connection connection = null;

    /**
     * Private constructor prevents external instantiation.
     * Loads the PostgreSQL JDBC driver.
     */
    private DBConnection() {
        try {
            // Load PostgreSQL JDBC Driver
           Class.forName("org.postgresql.Driver");

            System.out.println("[DBConnection] PostgreSQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] ERROR: PostgreSQL JDBC Driver not found!");
            System.err.println("Make sure postgresql.jar is in your classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Returns a singleton database connection.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                new DBConnection(); // Load driver
                connection = DriverManager.getConnection(
                        DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("[DBConnection] Connected to PostgreSQL successfully.");
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection] ERROR: Failed to connect to PostgreSQL!");
            System.err.println("Check if PostgreSQL service is running and credentials are correct.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Close database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DBConnection] Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DBConnection] ERROR: Failed to close connection.");
            e.printStackTrace();
        }
    }
}
