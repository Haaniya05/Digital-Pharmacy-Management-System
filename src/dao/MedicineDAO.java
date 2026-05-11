package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Medicine;

/**
 * MedicineDAO.java - Data Access Object for Medicine operations.
 * 
 * Handles all database interactions related to the 'medicines' table including
 * full CRUD operations, search functionality, and stock/expiry monitoring
 * queries.
 * This is the most important DAO in the system as medicines are central to all
 * operations.
 */
public class MedicineDAO {

    /**
     * Adds a new medicine to the inventory.
     * 
     * @param med The Medicine object to insert
     * @return true if insertion was successful
     */
    public boolean addMedicine(Medicine med) {
        String sql = "INSERT INTO medicines (medicine_name, generic_name, category, batch_no, " +
                "price, quantity, mfg_date, exp_date, supplier, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, med.getMedicineName());
            pst.setString(2, med.getGenericName());
            pst.setString(3, med.getCategory());
            pst.setString(4, med.getBatchNo());
            pst.setDouble(5, med.getPrice());
            pst.setInt(6, med.getQuantity());
           pst.setDate(7, java.sql.Date.valueOf(med.getMfgDate()));
           pst.setDate(8, java.sql.Date.valueOf(med.getExpDate()));

            pst.setString(9, med.getSupplier());
            pst.setString(10, med.getDescription());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error adding medicine: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates an existing medicine's details.
     * 
     * @param med The Medicine object with updated values (must have valid
     *            medicineId)
     * @return true if update was successful
     */
    public boolean updateMedicine(Medicine med) {
        String sql = "UPDATE medicines SET medicine_name=?, generic_name=?, category=?, batch_no=?, " +
                "price=?, quantity=?, mfg_date=?, exp_date=?, supplier=?, description=? " +
                "WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, med.getMedicineName());
            pst.setString(2, med.getGenericName());
            pst.setString(3, med.getCategory());
            pst.setString(4, med.getBatchNo());
            pst.setDouble(5, med.getPrice());
            pst.setInt(6, med.getQuantity());
            pst.setDate(7, java.sql.Date.valueOf(med.getMfgDate()));
             pst.setDate(8, java.sql.Date.valueOf(med.getExpDate()));
            pst.setString(9, med.getSupplier());
            pst.setString(10, med.getDescription());
            pst.setInt(11, med.getMedicineId());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error updating medicine: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a medicine from the inventory by its ID.
     * 
     * @param medicineId The ID of the medicine to delete
     * @return true if deletion was successful
     */
    public boolean deleteMedicine(int medicineId) {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, medicineId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error deleting medicine: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fetches a single medicine by its ID.
     * 
     * @param medicineId The ID to search for
     * @return Medicine object if found, null otherwise
     */
    public Medicine getMedicineById(int medicineId) {
        String sql = "SELECT * FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, medicineId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return extractMedicine(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error fetching medicine: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all medicines from the database, ordered alphabetically.
     * 
     * @return List of all Medicine objects
     */
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines ORDER BY medicine_name";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                medicines.add(extractMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error fetching all medicines: " + e.getMessage());
            e.printStackTrace();
        }
        return medicines;
    }

    /**
     * Searches medicines by name (partial match using SQL LIKE).
     * Used by the search functionality in MedicineForm.
     * 
     * @param keyword The search keyword
     * @return List of matching Medicine objects
     */
    public List<Medicine> searchByName(String keyword) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE medicine_name LIKE ? OR generic_name LIKE ? ORDER BY medicine_name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                medicines.add(extractMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error searching medicines: " + e.getMessage());
            e.printStackTrace();
        }
        return medicines;
    }

    /**
     * Retrieves medicines with stock quantity below the specified threshold.
     * Used by the Stock Monitor panel to show low-stock alerts.
     * 
     * @param threshold The minimum quantity threshold
     * @return List of low-stock Medicine objects
     */
    public List<Medicine> getLowStock(int threshold) {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE quantity <= ? ORDER BY quantity ASC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, threshold);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                medicines.add(extractMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error fetching low stock: " + e.getMessage());
            e.printStackTrace();
        }
        return medicines;
    }

    /**
     * Retrieves medicines expiring within the specified number of days.
     * Used by the Stock Monitor panel to show expiry warnings.
     * 
     * @param days Number of days from today to check
     * @return List of expiring Medicine objects
     */
    public List<Medicine> getExpiringSoon(int days) {
        List<Medicine> medicines = new ArrayList<>();
       String sql = "SELECT * FROM medicines WHERE exp_date BETWEEN CURRENT_DATE AND CURRENT_DATE + (? * INTERVAL '1 day') ORDER BY exp_date ASC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, days);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                medicines.add(extractMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error fetching expiring medicines: " + e.getMessage());
            e.printStackTrace();
        }
        return medicines;
    }

    /**
     * Retrieves medicines that have already expired (exp_date < today).
     * Used by the Stock Monitor panel to show expired items in red.
     * 
     * @return List of expired Medicine objects
     */
    public List<Medicine> getExpired() {
        List<Medicine> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE exp_date < CURRENT_DATE ORDER BY exp_date ASC";

        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                medicines.add(extractMedicine(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error fetching expired medicines: " + e.getMessage());
            e.printStackTrace();
        }
        return medicines;
    }

    /**
     * Gets the total count of distinct medicines in the inventory.
     * Used by the Dashboard for summary statistics.
     * 
     * @return Total number of medicine records
     */
    public int getTotalMedicineCount() {
        String sql = "SELECT COUNT(*) AS total FROM medicines";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("[MedicineDAO] Error counting medicines: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Helper method to extract a Medicine object from a ResultSet row.
     */
    private Medicine extractMedicine(ResultSet rs) throws SQLException {
        return new Medicine(
                rs.getInt("medicine_id"),
                rs.getString("medicine_name"),
                rs.getString("generic_name"),
                rs.getString("category"),
                rs.getString("batch_no"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getDate("mfg_date").toString(),
                rs.getDate("exp_date").toString(),

                rs.getString("supplier"),
                rs.getString("description"));
    }
}
