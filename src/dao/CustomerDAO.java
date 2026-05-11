package dao;

import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO.java - Data Access Object for Customer operations.
 * 
 * Handles all database interactions related to the 'customers' table including
 * full CRUD operations and search functionality.
 */
public class CustomerDAO {

    /**
     * Adds a new customer to the database.
     * 
     * @param customer The Customer object to insert
     * @return true if insertion was successful
     */
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, customer.getCustomerName());
            pst.setString(2, customer.getPhone());
            pst.setString(3, customer.getEmail());
            pst.setString(4, customer.getAddress());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error adding customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing customer's details.
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET customer_name=?, phone=?, email=?, address=? WHERE customer_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, customer.getCustomerName());
            pst.setString(2, customer.getPhone());
            pst.setString(3, customer.getEmail());
            pst.setString(4, customer.getAddress());
            pst.setInt(5, customer.getCustomerId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a customer by their ID.
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error deleting customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all customers from the database.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY customer_name";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(extractCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error fetching customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Searches customers by name (partial match).
     */
    public List<Customer> searchByName(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_name LIKE ? ORDER BY customer_name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                customers.add(extractCustomer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error searching customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Fetches a single customer by ID.
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return extractCustomer(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error fetching customer: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets total customer count for dashboard statistics.
     */
    public int getTotalCustomerCount() {
        String sql = "SELECT COUNT(*) AS total FROM customers";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("[CustomerDAO] Error counting customers: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to extract a Customer object from a ResultSet row.
     */
    private Customer extractCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"));
    }
}
