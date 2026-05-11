package gui;

import dao.MedicineDAO;
import dao.SaleDAO;
import model.Medicine;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ReportsPanel.java - Reporting dashboard for pharmacy analytics.
 * 
 * Features:
 * - Daily sales report (by date)
 * - Monthly revenue summary
 * - Low stock report
 * - Expiry report
 * - Summary statistics
 */
public class ReportsPanel extends JFrame {

    private MedicineDAO medicineDAO = new MedicineDAO();
    private SaleDAO saleDAO = new SaleDAO();

    private JTextField txtDate;
    private JComboBox<String> cmbMonth, cmbYear;
    private JTable tblReport;
    private DefaultTableModel modelReport;
    private JLabel lblSummary;

    public ReportsPanel() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Pharmacy Reports");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 58, 64));
        headerPanel.setPreferredSize(new Dimension(950, 45));
        JLabel lblHeader = new JLabel("Pharmacy Reports & Analytics");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== Left: Report Options ====================
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        optionsPanel.setPreferredSize(new Dimension(250, 0));

        // --- Daily Sales ---
        JLabel lblDaily = new JLabel("Daily Sales Report");
        lblDaily.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDaily.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(lblDaily);
        optionsPanel.add(Box.createVerticalStrut(5));

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.setOpaque(false);
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePanel.setMaximumSize(new Dimension(230, 30));
        txtDate = new JTextField(10);
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        datePanel.add(txtDate);
        JButton btnDaily = styledBtn("Go", new Color(0, 123, 255));
        btnDaily.setPreferredSize(new Dimension(55, 28));
        datePanel.add(btnDaily);
        optionsPanel.add(datePanel);
        optionsPanel.add(Box.createVerticalStrut(15));

        // --- Monthly Revenue ---
        JLabel lblMonthly = new JLabel("Monthly Revenue");
        lblMonthly.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMonthly.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(lblMonthly);
        optionsPanel.add(Box.createVerticalStrut(5));

        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        monthPanel.setOpaque(false);
        monthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthPanel.setMaximumSize(new Dimension(230, 30));

        String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        cmbMonth = new JComboBox<>(months);
        cmbMonth.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        monthPanel.add(cmbMonth);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String[] years = new String[5];
        for (int i = 0; i < 5; i++)
            years[i] = String.valueOf(currentYear - i);
        cmbYear = new JComboBox<>(years);
        monthPanel.add(cmbYear);

        JButton btnMonthly = styledBtn("Go", new Color(40, 167, 69));
        btnMonthly.setPreferredSize(new Dimension(55, 28));
        monthPanel.add(btnMonthly);
        optionsPanel.add(monthPanel);
        optionsPanel.add(Box.createVerticalStrut(20));

        // --- Quick Reports ---
        JLabel lblQuick = new JLabel("Quick Reports");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQuick.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsPanel.add(lblQuick);
        optionsPanel.add(Box.createVerticalStrut(8));

        JButton btnLowStock = styledBtn("Low Stock Report", new Color(255, 165, 0));
        btnLowStock.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLowStock.setMaximumSize(new Dimension(220, 35));
        optionsPanel.add(btnLowStock);
        optionsPanel.add(Box.createVerticalStrut(8));

        JButton btnExpiring = styledBtn("Expiring Soon Report", new Color(220, 53, 69));
        btnExpiring.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExpiring.setMaximumSize(new Dimension(220, 35));
        optionsPanel.add(btnExpiring);
        optionsPanel.add(Box.createVerticalStrut(8));

        JButton btnExpired = styledBtn("Expired Medicines", new Color(128, 0, 0));
        btnExpired.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExpired.setMaximumSize(new Dimension(220, 35));
        optionsPanel.add(btnExpired);
        optionsPanel.add(Box.createVerticalStrut(8));

        JButton btnAllMeds = styledBtn("All Medicines Summary", new Color(0, 102, 153));
        btnAllMeds.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAllMeds.setMaximumSize(new Dimension(220, 35));
        optionsPanel.add(btnAllMeds);

        mainPanel.add(optionsPanel, BorderLayout.WEST);

        // ==================== Center: Report Table ====================
        JPanel reportPanel = new JPanel(new BorderLayout(0, 8));
        reportPanel.setOpaque(false);

        lblSummary = new JLabel("  Select a report from the left panel");
        lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reportPanel.add(lblSummary, BorderLayout.NORTH);

        modelReport = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblReport = new JTable(modelReport);
        tblReport.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblReport.setRowHeight(25);
        tblReport.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblReport.getTableHeader().setBackground(new Color(52, 58, 64));
        tblReport.getTableHeader().setForeground(Color.WHITE);

        reportPanel.add(new JScrollPane(tblReport), BorderLayout.CENTER);
        mainPanel.add(reportPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // ==================== Event Listeners ====================
        btnDaily.addActionListener(e -> showDailySales());
        btnMonthly.addActionListener(e -> showMonthlyRevenue());
        btnLowStock.addActionListener(e -> showLowStockReport());
        btnExpiring.addActionListener(e -> showExpiringReport());
        btnExpired.addActionListener(e -> showExpiredReport());
        btnAllMeds.addActionListener(e -> showAllMedicines());
    }

    private JButton styledBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showDailySales() {
        String date = txtDate.getText().trim();
        List<Object[]> data = saleDAO.getDailySalesReport(date);

        String[] cols = { "Sale ID", "Customer", "Total (Rs.)", "Payment", "Date/Time" };
        modelReport.setColumnCount(0);
        modelReport.setRowCount(0);
        for (String col : cols)
            modelReport.addColumn(col);

        double total = 0;
        for (Object[] row : data) {
            modelReport.addRow(row);
            total += (double) row[2];
        }

        lblSummary.setText("  Daily Sales Report for " + date + " | Total: Rs. " + String.format("%.2f", total)
                + " | Transactions: " + data.size());
    }

    private void showMonthlyRevenue() {
        int month = cmbMonth.getSelectedIndex() + 1;
        int year = Integer.parseInt(cmbYear.getSelectedItem().toString());
        double total = saleDAO.getMonthlySalesTotal(month, year);
        int count = saleDAO.getTotalSalesCount();

        String[] cols = { "Metric", "Value" };
        modelReport.setColumnCount(0);
        modelReport.setRowCount(0);
        for (String col : cols)
            modelReport.addColumn(col);

        modelReport.addRow(new Object[] { "Month", cmbMonth.getSelectedItem() + " " + year });
        modelReport.addRow(new Object[] { "Total Revenue", "Rs. " + String.format("%.2f", total) });
        modelReport.addRow(new Object[] { "Total Sales (All Time)", count });

        lblSummary.setText("  Monthly Revenue Report | " + cmbMonth.getSelectedItem() + " " + year + ": Rs. "
                + String.format("%.2f", total));
    }

    private void showLowStockReport() {
        List<Medicine> meds = medicineDAO.getLowStock(10);
        showMedicineReport(meds, "Low Stock Report (Quantity ≤ 10)", "low stock items");
    }

    private void showExpiringReport() {
        List<Medicine> meds = medicineDAO.getExpiringSoon(30);
        showMedicineReport(meds, "Expiring Soon Report (within 30 days)", "items expiring soon");
    }

    private void showExpiredReport() {
        List<Medicine> meds = medicineDAO.getExpired();
        showMedicineReport(meds, "Expired Medicines Report", "expired items");
    }

    private void showAllMedicines() {
        List<Medicine> meds = medicineDAO.getAllMedicines();
        showMedicineReport(meds, "All Medicines Summary", "medicines in inventory");
    }

    private void showMedicineReport(List<Medicine> meds, String title, String itemLabel) {
        String[] cols = { "ID", "Name", "Generic", "Category", "Batch", "Price", "Qty", "Exp Date", "Supplier" };
        modelReport.setColumnCount(0);
        modelReport.setRowCount(0);
        for (String col : cols)
            modelReport.addColumn(col);

        for (Medicine m : meds) {
            modelReport.addRow(new Object[] {
                    m.getMedicineId(), m.getMedicineName(), m.getGenericName(), m.getCategory(),
                    m.getBatchNo(), m.getPrice(), m.getQuantity(), m.getExpDate(), m.getSupplier()
            });
        }

        lblSummary.setText("  " + title + " | " + meds.size() + " " + itemLabel);
    }
}
