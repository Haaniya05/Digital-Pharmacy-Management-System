package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Sale;
import model.SaleItem;

/**
 * SaleDAO.java - Data Access Object for Sales operations.
 * 
 * Handles sales as transactional units: creates sale header, inserts line
 * items,
 * and decrements medicine stock — all within a single database transaction.
 * If any step fails, the entire operation is rolled back to prevent data
 * inconsistency.
 */
public class SaleDAO {

    /**
     * Processes a complete sale transaction:
     * 1. Inserts the sale header record
     * 2. Inserts each sale line item
     * 3. Decrements stock quantity for each sold medicine
     * All steps run within a single transaction for data integrity.
     * 
     * @param sale  The Sale header object
     * @param items List of SaleItem line items
     * @return true if the entire transaction was successful
     */
    public boolean addSale(Sale sale, List<SaleItem> items) {
        String sqlHeader = "INSERT INTO sales (customer_id, sale_date, total_amount, payment_mode, sold_by) VALUES (?, NOW(), ?, ?, ?)";
        String sqlItem = "INSERT INTO sale_items (sale_id, medicine_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE medicines SET quantity = quantity - ? WHERE medicine_id = ? AND quantity >= ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert sale header
            PreparedStatement pstHeader = conn.prepareStatement(sqlHeader, Statement.RETURN_GENERATED_KEYS);
            if (sale.getCustomerId() > 0) {
                pstHeader.setInt(1, sale.getCustomerId());
            } else {
                pstHeader.setNull(1, Types.INTEGER); // Walk-in customer (no ID)
            }
            pstHeader.setDouble(2, sale.getTotalAmount());
            pstHeader.setString(3, sale.getPaymentMode());
            pstHeader.setInt(4, sale.getSoldBy());
            pstHeader.executeUpdate();

            // Get auto-generated sale_id
            ResultSet generatedKeys = pstHeader.getGeneratedKeys();
            int saleId = 0;
            if (generatedKeys.next()) {
                saleId = generatedKeys.getInt(1);
            }

            // 2. Insert sale items and 3. Update stock
            PreparedStatement pstItem = conn.prepareStatement(sqlItem);
            PreparedStatement pstStock = conn.prepareStatement(sqlUpdateStock);

            for (SaleItem item : items) {
                // Insert line item
                pstItem.setInt(1, saleId);
                pstItem.setInt(2, item.getMedicineId());
                pstItem.setInt(3, item.getQuantity());
                pstItem.setDouble(4, item.getUnitPrice());
                pstItem.setDouble(5, item.getTotalPrice());
                pstItem.addBatch();

                // Decrement stock (with check: quantity >= sold amount)
                pstStock.setInt(1, item.getQuantity());
                pstStock.setInt(2, item.getMedicineId());
                pstStock.setInt(3, item.getQuantity());
                int updated = pstStock.executeUpdate();

                if (updated == 0) {
                    // Stock insufficient — rollback entire transaction
                    conn.rollback();
                    System.err.println("[SaleDAO] Insufficient stock for medicine ID: " + item.getMedicineId());
                    return false;
                }
            }
            pstItem.executeBatch();

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error processing sale: " + e.getMessage());
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Retrieves all sales with customer names (via LEFT JOIN).
     */
    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT s.*, c.customer_name, u.full_name AS sold_by_name FROM sales s " +
                "LEFT JOIN customers c ON s.customer_id = c.customer_id " +
                "LEFT JOIN users u ON s.sold_by = u.user_id " +
                "ORDER BY s.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

while (rs.next()) {
    sales.add(new Sale(
        rs.getInt("sale_id"),
        rs.getInt("customer_id"),
        rs.getString("customer_name") != null
            ? rs.getString("customer_name")
            : "Walk-in",
        rs.getDate("sale_date"),   
        rs.getDouble("total_amount"),
        rs.getString("payment_mode"),
        rs.getInt("sold_by"),
        rs.getString("sold_by_name")
    ));
}

        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error fetching sales: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    /**
     * Retrieves sale items for a specific sale (with medicine names via JOIN).
     */
    public List<SaleItem> getItemsBySaleId(int saleId) {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT si.*, m.medicine_name FROM sale_items si " +
                "JOIN medicines m ON si.medicine_id = m.medicine_id " +
                "WHERE si.sale_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, saleId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                items.add(new SaleItem(
                        rs.getInt("item_id"),
                        rs.getInt("sale_id"),
                        rs.getInt("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("total_price")));
            }
        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error fetching sale items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Gets today's total sales amount for dashboard display.
     */
    public double getTodaySalesTotal() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total " +
                 "FROM sales WHERE sale_date::date = CURRENT_DATE";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            if (rs.next())
                return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error fetching today's sales: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Gets monthly total sales for reporting.
     */
    public double getMonthlySalesTotal(int month, int year) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total " +
                 "FROM sales " +
                 "WHERE EXTRACT(MONTH FROM sale_date) = ? " +
                 "AND EXTRACT(YEAR FROM sale_date) = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, month);
            pst.setInt(2, year);
            ResultSet rs = pst.executeQuery();

            if (rs.next())
                return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error fetching monthly sales: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Gets total number of sales for dashboard statistics.
     */
    public int getTotalSalesCount() {
        String sql = "SELECT COUNT(*) AS total FROM sales";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error counting sales: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Gets daily sales breakdown for reports.
     */
    public List<Object[]> getDailySalesReport(String date) {
        List<Object[]> report = new ArrayList<>();
        String sql =  "SELECT s.sale_id, c.customer_name, s.total_amount, " +
                 "s.payment_mode, s.sale_date " +
                 "FROM sales s " +
                 "LEFT JOIN customers c ON s.customer_id = c.customer_id " +
                 "WHERE s.sale_date::date = ? " +
                 "ORDER BY s.sale_date";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                report.add(new Object[] {
                        rs.getInt("sale_id"),
                        rs.getString("customer_name") != null ? rs.getString("customer_name") : "Walk-in",
                        rs.getDouble("total_amount"),
                        rs.getString("payment_mode"),
                       rs.getTimestamp("sale_date")

                });
            }
        } catch (SQLException e) {
            System.err.println("[SaleDAO] Error fetching daily report: " + e.getMessage());
        }
        return report;
    }
}
