package gui;

import dao.MedicineDAO;
import model.Medicine;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * StockMonitorPanel.java - Stock and Expiry monitoring dashboard.
 *
 * Features:
 * - Tab 1: Low Stock items (quantity <= 10) highlighted in amber
 * - Tab 2: Expiring Soon items (within 30 days) highlighted in orange
 * - Tab 3: Already Expired items highlighted in red
 * - Refresh button to reload data
 */
public class StockMonitorPanel extends JFrame {

    private MedicineDAO medicineDAO = new MedicineDAO();
    private JTable tblLowStock, tblExpiringSoon, tblExpired;
    private DefaultTableModel modelLow, modelExpiring, modelExpired;

    public StockMonitorPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Stock & Expiry Monitor");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 165, 0));
        headerPanel.setPreferredSize(new Dimension(900, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lblHeader = new JLabel("Stock & Expiry Monitor");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.WEST);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(Color.WHITE);
        btnRefresh.setForeground(new Color(255, 165, 0));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(btnRefresh);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Low Stock
        String[] columns = { "ID", "Medicine Name", "Generic Name", "Category", "Batch", "Price", "Quantity",
                "Exp Date", "Supplier" };

        modelLow = createTableModel(columns);
        tblLowStock = createStyledTable(modelLow, new Color(255, 193, 7, 80)); // Amber
        tabs.addTab("⚠ Low Stock (Qty ≤ 10)", new JScrollPane(tblLowStock));

        // Tab 2: Expiring Soon
        modelExpiring = createTableModel(columns);
        tblExpiringSoon = createStyledTable(modelExpiring, new Color(255, 140, 0, 80)); // Orange
        tabs.addTab("⏰ Expiring Soon (30 days)", new JScrollPane(tblExpiringSoon));

        // Tab 3: Expired
        modelExpired = createTableModel(columns);
        tblExpired = createStyledTable(modelExpired, new Color(220, 53, 69, 80)); // Red
        tabs.addTab("❌ Expired", new JScrollPane(tblExpired));

        mainPanel.add(tabs, BorderLayout.CENTER);

        // Summary panel at bottom
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        summaryPanel.setBackground(new Color(240, 248, 255));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
    }

    private JTable createStyledTable(DefaultTableModel model, Color rowColor) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 58, 64));
        table.getTableHeader().setForeground(Color.WHITE);

        // Color all rows with the alert color
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? rowColor
                            : new Color(rowColor.getRed(), rowColor.getGreen(), rowColor.getBlue(), 40));
                }
                return c;
            }
        });

        return table;
    }

    /**
     * Loads data into all three tabs from the database.
     */
    private void loadData() {
        // Low Stock (quantity <= 10)
        modelLow.setRowCount(0);
        List<Medicine> lowStock = medicineDAO.getLowStock(10);
        for (Medicine m : lowStock) {
            modelLow.addRow(new Object[] {
                    m.getMedicineId(), m.getMedicineName(), m.getGenericName(), m.getCategory(),
                    m.getBatchNo(), m.getPrice(), m.getQuantity(), m.getExpDate(), m.getSupplier()
            });
        }

        // Expiring Soon (within 30 days)
        modelExpiring.setRowCount(0);
        List<Medicine> expiring = medicineDAO.getExpiringSoon(30);
        for (Medicine m : expiring) {
            modelExpiring.addRow(new Object[] {
                    m.getMedicineId(), m.getMedicineName(), m.getGenericName(), m.getCategory(),
                    m.getBatchNo(), m.getPrice(), m.getQuantity(), m.getExpDate(), m.getSupplier()
            });
        }

        // Expired
        modelExpired.setRowCount(0);
        List<Medicine> expired = medicineDAO.getExpired();
        for (Medicine m : expired) {
            modelExpired.addRow(new Object[] {
                    m.getMedicineId(), m.getMedicineName(), m.getGenericName(), m.getCategory(),
                    m.getBatchNo(), m.getPrice(), m.getQuantity(), m.getExpDate(), m.getSupplier()
            });
        }
    }
}
