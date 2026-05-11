package gui;

import dao.*;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * DashboardForm.java - The main navigation hub of the application.
 * 
 * Displays summary statistics (total medicines, low stock alerts, expiring
 * soon,
 * today's sales total) and provides navigation buttons to all other modules.
 * This form opens after successful login.
 */
public class DashboardForm extends JFrame {

    private User currentUser;
    private MedicineDAO medicineDAO = new MedicineDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private SaleDAO saleDAO = new SaleDAO();
    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    // Summary card labels
    private JLabel lblTotalMedicines;
    private JLabel lblLowStock;
    private JLabel lblExpiringSoon;
    private JLabel lblTodaySales;
    private JLabel lblTotalCustomers;
    private JLabel lblTotalPrescriptions;

    public DashboardForm(User user) {
        this.currentUser = user;
        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        setTitle("Digital Pharmacy Management System - Dashboard");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(new Color(236, 240, 245));

        // ==================== Top Header Bar ====================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 153));
        headerPanel.setPreferredSize(new Dimension(1000, 65));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblAppTitle = new JLabel("Digital Pharmacy Management System");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAppTitle.setForeground(Color.WHITE);
        headerPanel.add(lblAppTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel lblWelcome = new JLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(new Color(200, 230, 255));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        userPanel.add(lblWelcome);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(btnLogout);
        headerPanel.add(userPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== Center Panel (Cards + Navigation) ====================
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Summary Cards Panel (top section)
        JPanel cardsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(new Dimension(900, 180));

        lblTotalMedicines = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Total Medicines", lblTotalMedicines, new Color(0, 123, 255)));

        lblLowStock = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Low Stock Alerts", lblLowStock, new Color(255, 165, 0)));

        lblExpiringSoon = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Expiring Soon", lblExpiringSoon, new Color(220, 53, 69)));

        lblTodaySales = new JLabel("Rs. 0.00", SwingConstants.CENTER);
        cardsPanel.add(createCard("Today's Sales", lblTodaySales, new Color(40, 167, 69)));

        lblTotalCustomers = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Total Customers", lblTotalCustomers, new Color(111, 66, 193)));

        lblTotalPrescriptions = new JLabel("0", SwingConstants.CENTER);
        cardsPanel.add(createCard("Prescriptions", lblTotalPrescriptions, new Color(23, 162, 184)));

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Navigation Buttons Panel (center section)
        JPanel navPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        navPanel.add(createNavButton("Manage Medicines", new Color(0, 123, 255), e -> openMedicineForm()));
        navPanel.add(createNavButton("Stock Monitor", new Color(255, 165, 0), e -> openStockMonitor()));
        navPanel.add(createNavButton("Manage Customers", new Color(111, 66, 193), e -> openCustomerForm()));
        navPanel.add(createNavButton("Prescriptions", new Color(23, 162, 184), e -> openPrescriptionForm()));
        navPanel.add(createNavButton("New Sale", new Color(40, 167, 69), e -> openSalesForm()));
        navPanel.add(createNavButton("Reports", new Color(52, 58, 64), e -> openReportsPanel()));
        navPanel.add(createNavButton("Refresh Dashboard", new Color(0, 102, 153), e -> loadDashboardData()));

        centerPanel.add(navPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==================== Footer ====================
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(0, 102, 153));
        footerPanel.setPreferredSize(new Dimension(1000, 30));
        JLabel lblFooter = new JLabel(
                "© 2026 Digital Pharmacy Management System | Logged in as: " + currentUser.getUsername());
        lblFooter.setForeground(new Color(200, 230, 255));
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerPanel.add(lblFooter);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Creates a styled summary card with a title, value label, and background
     * color.
     */
    private JPanel createCard(String title, JLabel valueLabel, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(255, 255, 255, 200));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Creates a styled navigation button with a hover effect.
     */
    private JButton createNavButton(String text, Color bgColor, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Hover effect
        Color hoverColor = bgColor.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        btn.addActionListener(action);
        return btn;
    }

    /**
     * Loads summary data from the database and updates the dashboard cards.
     */
    private void loadDashboardData() {
        try {
            lblTotalMedicines.setText(String.valueOf(medicineDAO.getTotalMedicineCount()));
            lblLowStock.setText(String.valueOf(medicineDAO.getLowStock(10).size()));
            lblExpiringSoon.setText(String.valueOf(medicineDAO.getExpiringSoon(30).size()));
            lblTodaySales.setText("Rs. " + String.format("%.2f", saleDAO.getTodaySalesTotal()));
            lblTotalCustomers.setText(String.valueOf(customerDAO.getTotalCustomerCount()));
            lblTotalPrescriptions.setText(String.valueOf(prescriptionDAO.getTotalPrescriptionCount()));
        } catch (Exception e) {
            System.err.println("[Dashboard] Error loading data: " + e.getMessage());
        }
    }

    // ==================== Navigation Methods ====================

    private void openMedicineForm() {
        new MedicineForm().setVisible(true);
    }

    private void openStockMonitor() {
        new StockMonitorPanel().setVisible(true);
    }

    private void openCustomerForm() {
        new CustomerForm().setVisible(true);
    }

    private void openPrescriptionForm() {
        new PrescriptionForm().setVisible(true);
    }

    private void openSalesForm() {
        new SalesForm(currentUser).setVisible(true);
    }

    private void openReportsPanel() {
        new ReportsPanel().setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginForm().setVisible(true);
            dispose();
        }
    }
}
