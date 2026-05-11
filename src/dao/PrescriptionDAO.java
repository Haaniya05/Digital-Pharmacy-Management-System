package dao;

import model.Prescription;
import model.PrescriptionItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PrescriptionDAO.java - Data Access Object for Prescription operations.
 * 
 * Handles prescriptions as transactional units: a prescription header with
 * multiple line items is inserted as a single atomic operation. This ensures
 * data consistency — either all items are saved or none.
 */
public class PrescriptionDAO {

    /**
     * Adds a new prescription with its items as a single transaction.
     * Uses JDBC transaction management (setAutoCommit(false) + commit/rollback).
     * 
     * @param prescription The Prescription header
     * @param items        List of PrescriptionItem line items
     * @return true if the entire transaction was successful
     */
    public boolean addPrescription(Prescription prescription, List<PrescriptionItem> items) {
        String sqlHeader = "INSERT INTO prescriptions (customer_id, doctor_name, prescription_date, notes) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO prescription_items (prescription_id, medicine_id, quantity, dosage) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Insert prescription header and get generated ID
            PreparedStatement pstHeader = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            pstHeader.setInt(1, prescription.getCustomerId());
            pstHeader.setString(2, prescription.getDoctorName());
            pstHeader.setString(3, prescription.getPrescriptionDate());
            pstHeader.setString(4, prescription.getNotes());
            pstHeader.executeUpdate();

            // Get the auto-generated prescription_id
            ResultSet generatedKeys = pstHeader.getGeneratedKeys();
            int prescriptionId = 0;
            if (generatedKeys.next()) {
                prescriptionId = generatedKeys.getInt(1);
            }

            // Insert each prescription item
            PreparedStatement pstItem = conn.prepareStatement(sqlItem);
            for (PrescriptionItem item : items) {
                pstItem.setInt(1, prescriptionId);
                pstItem.setInt(2, item.getMedicineId());
                pstItem.setInt(3, item.getQuantity());
                pstItem.setString(4, item.getDosage());
                pstItem.addBatch();
            }
            pstItem.executeBatch();

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("[PrescriptionDAO] Error adding prescription: " + e.getMessage());
            try {
                if (conn != null)
                    conn.rollback(); // Rollback on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves all prescriptions with customer names (via JOIN).
     */
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, c.customer_name FROM prescriptions p " +
                "LEFT JOIN customers c ON p.customer_id = c.customer_id " +
                "ORDER BY p.prescription_date DESC";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                prescriptions.add(new Prescription(
                        rs.getInt("prescription_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("doctor_name"),
                        rs.getString("prescription_date"),
                        rs.getString("notes")));
            }
        } catch (SQLException e) {
            System.err.println("[PrescriptionDAO] Error fetching prescriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return prescriptions;
    }

    /**
     * Retrieves prescriptions for a specific customer.
     */
    public List<Prescription> getByCustomerId(int customerId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT p.*, c.customer_name FROM prescriptions p " +
                "LEFT JOIN customers c ON p.customer_id = c.customer_id " +
                "WHERE p.customer_id = ? ORDER BY p.prescription_date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, customerId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                prescriptions.add(new Prescription(
                        rs.getInt("prescription_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("doctor_name"),
                        rs.getString("prescription_date"),
                        rs.getString("notes")));
            }
        } catch (SQLException e) {
            System.err.println("[PrescriptionDAO] Error fetching customer prescriptions: " + e.getMessage());
            e.printStackTrace();
        }
        return prescriptions;
    }

    /**
     * Retrieves line items for a specific prescription (with medicine names via
     * JOIN).
     */
    public List<PrescriptionItem> getItemsByPrescriptionId(int prescriptionId) {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT pi.*, m.medicine_name FROM prescription_items pi " +
                "JOIN medicines m ON pi.medicine_id = m.medicine_id " +
                "WHERE pi.prescription_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, prescriptionId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                items.add(new PrescriptionItem(
                        rs.getInt("item_id"),
                        rs.getInt("prescription_id"),
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getInt("quantity"),
                        rs.getString("dosage")));
            }
        } catch (SQLException e) {
            System.err.println("[PrescriptionDAO] Error fetching prescription items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Gets total prescription count for dashboard statistics.
     */
    public int getTotalPrescriptionCount() {
        String sql = "SELECT COUNT(*) AS total FROM prescriptions";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("[PrescriptionDAO] Error counting prescriptions: " + e.getMessage());
        }
        return 0;
    }
}
