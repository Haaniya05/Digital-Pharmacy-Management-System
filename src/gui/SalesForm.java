package gui;

import dao.CustomerDAO;
import dao.MedicineDAO;
import dao.SaleDAO;
import model.Customer;
import model.Medicine;
import model.Sale;
import model.SaleItem;
import model.User;
import util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SalesForm.java - Interface for processing medicine sales.
 * 
 * Features:
 * - Select customer (or walk-in)
 * - Add multiple medicines with auto-calculated totals
 * - Choose payment mode (Cash/Card/UPI)
 * - Auto-decrement stock on sale
 * - Display receipt-style summary
 */
public class SalesForm extends JFrame {

    private User currentUser;

    // Form fields
    private JComboBox<String> cmbCustomer;
    private JComboBox<String> cmbMedicine;
    private JTextField txtQty;
    private JComboBox<String> cmbPayment;
    private JLabel lblGrandTotal;

    // Cart table
    private JTable tblCart;
    private DefaultTableModel modelCart;

    // Data
    private List<Customer> customerList;
    private List<Medicine> medicineList;
    private List<SaleItem> cartItems = new ArrayList<>();
    private double grandTotal = 0.0;

    // DAOs
    private CustomerDAO customerDAO = new CustomerDAO();
    private MedicineDAO medicineDAO = new MedicineDAO();
    private SaleDAO saleDAO = new SaleDAO();

    public SalesForm(User user) {
        this.currentUser = user;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Sales / Billing");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 167, 69));
        headerPanel.setPreferredSize(new Dimension(900, 45));
        JLabel lblHeader = new JLabel("New Sale / Bill");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== Top: Sale Info ====================
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Sale Information"));

        infoPanel.add(new JLabel("Customer:"));
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setPreferredSize(new Dimension(200, 28));
        infoPanel.add(cmbCustomer);

        infoPanel.add(Box.createHorizontalStrut(15));
        infoPanel.add(new JLabel("Payment:"));
        cmbPayment = new JComboBox<>(new String[] { "Cash", "Card", "UPI" });
        cmbPayment.setPreferredSize(new Dimension(100, 28));
        infoPanel.add(cmbPayment);

        infoPanel.add(Box.createHorizontalStrut(15));
        infoPanel.add(new JLabel("Billed by:"));
        JLabel lblBiller = new JLabel(currentUser.getFullName());
        lblBiller.setFont(new Font("Segoe UI", Font.BOLD, 12));
        infoPanel.add(lblBiller);

        // ==================== Middle: Add Item Section ====================
        JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        addItemPanel.setBackground(new Color(248, 249, 250));
        addItemPanel.setBorder(BorderFactory.createTitledBorder("Add Item to Cart"));

        addItemPanel.add(new JLabel("Medicine:"));
        cmbMedicine = new JComboBox<>();
        cmbMedicine.setPreferredSize(new Dimension(280, 28));
        addItemPanel.add(cmbMedicine);

        addItemPanel.add(new JLabel("Qty:"));
        txtQty = new JTextField(5);
        txtQty.setText("1");
        addItemPanel.add(txtQty);

        JButton btnAddToCart = styledBtn("Add to Cart", new Color(0, 123, 255));
        addItemPanel.add(btnAddToCart);

        // Combine top panels
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        topPanel.setOpaque(false);
        topPanel.add(infoPanel);
        topPanel.add(addItemPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==================== Center: Cart Table ====================
        String[] columns = { "Medicine", "Qty", "Unit Price (Rs.)", "Total (Rs.)" };
        modelCart = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblCart = new JTable(modelCart);
        tblCart.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblCart.setRowHeight(28);
        tblCart.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblCart.getTableHeader().setBackground(new Color(40, 167, 69));
        tblCart.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tblCart);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // ==================== Bottom: Total & Actions ====================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Grand total display
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        totalPanel.setOpaque(false);
        JLabel lblTotalLabel = new JLabel("Grand Total: ");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblGrandTotal = new JLabel("Rs. 0.00");
        lblGrandTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblGrandTotal.setForeground(new Color(40, 167, 69));
        totalPanel.add(lblTotalLabel);
        totalPanel.add(lblGrandTotal);
        bottomPanel.add(totalPanel, BorderLayout.EAST);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setOpaque(false);

        JButton btnRemove = styledBtn("Remove Item", new Color(220, 53, 69));
        JButton btnClear = styledBtn("Clear Cart", new Color(108, 117, 125));
        JButton btnCompleteSale = styledBtn("Complete Sale", new Color(40, 167, 69));
        btnCompleteSale.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCompleteSale.setPreferredSize(new Dimension(160, 40));

        actionPanel.add(btnRemove);
        actionPanel.add(btnClear);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(btnCompleteSale);
        bottomPanel.add(actionPanel, BorderLayout.WEST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // ==================== Event Listeners ====================
        btnAddToCart.addActionListener(e -> addToCart());
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        btnCompleteSale.addActionListener(e -> completeSale());
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

    private void loadData() {
        // Load customers
        customerList = customerDAO.getAllCustomers();
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("-- Walk-in Customer --");
        for (Customer c : customerList) {
            cmbCustomer.addItem(c.getCustomerId() + " - " + c.getCustomerName());
        }

        // Load medicines
        medicineList = medicineDAO.getAllMedicines();
        cmbMedicine.removeAllItems();
        for (Medicine m : medicineList) {
            cmbMedicine.addItem(m.getMedicineId() + " - " + m.getMedicineName() + " (Rs." + m.getPrice() + ", Stock: "
                    + m.getQuantity() + ")");
        }
    }

    private void addToCart() {
        if (cmbMedicine.getSelectedIndex() < 0) {
            ValidationUtil.showError(this, "Select a medicine!");
            return;
        }
        if (ValidationUtil.isNotValidInteger(txtQty.getText(), "Quantity", this))
            return;

        int qty = Integer.parseInt(txtQty.getText().trim());
        Medicine selected = medicineList.get(cmbMedicine.getSelectedIndex());

        if (qty > selected.getQuantity()) {
            ValidationUtil.showError(this, "Insufficient stock! Available: " + selected.getQuantity());
            return;
        }
        if (qty <= 0) {
            ValidationUtil.showError(this, "Quantity must be greater than 0!");
            return;
        }

        double totalPrice = selected.getPrice() * qty;

        SaleItem item = new SaleItem();
        item.setMedicineId(selected.getMedicineId());
        item.setMedicineName(selected.getMedicineName());
        item.setQuantity(qty);
        item.setUnitPrice(selected.getPrice());
        item.setTotalPrice(totalPrice);
        cartItems.add(item);

        modelCart.addRow(new Object[] {
                selected.getMedicineName(), qty,
                String.format("%.2f", selected.getPrice()),
                String.format("%.2f", totalPrice)
        });

        grandTotal += totalPrice;
        lblGrandTotal.setText("Rs. " + String.format("%.2f", grandTotal));
        txtQty.setText("1");
    }

    private void removeFromCart() {
        int row = tblCart.getSelectedRow();
        if (row >= 0) {
            grandTotal -= cartItems.get(row).getTotalPrice();
            cartItems.remove(row);
            modelCart.removeRow(row);
            lblGrandTotal.setText("Rs. " + String.format("%.2f", grandTotal));
        } else {
            ValidationUtil.showError(this, "Select an item to remove!");
        }
    }

    private void clearCart() {
        cartItems.clear();
        modelCart.setRowCount(0);
        grandTotal = 0.0;
        lblGrandTotal.setText("Rs. 0.00");
    }

    private void completeSale() {
        if (cartItems.isEmpty()) {
            ValidationUtil.showError(this, "Cart is empty! Add items first.");
            return;
        }

        if (!ValidationUtil.confirmAction(this,
                "Complete this sale for Rs. " + String.format("%.2f", grandTotal) + "?")) {
            return;
        }

        // Determine customer ID (0 for walk-in)
        int customerId = 0;
        if (cmbCustomer.getSelectedIndex() > 0) {
            customerId = customerList.get(cmbCustomer.getSelectedIndex() - 1).getCustomerId();
        }

        Sale sale = new Sale();
        sale.setCustomerId(customerId);
        sale.setTotalAmount(grandTotal);
        sale.setPaymentMode(cmbPayment.getSelectedItem().toString());
        sale.setSoldBy(currentUser.getUserId());

        if (saleDAO.addSale(sale, cartItems)) {
            // Show receipt
            showReceipt(customerId);
            clearCart();
            loadData(); // Refresh stock numbers
        } else {
            ValidationUtil.showError(this, "Sale failed! Check stock availability.");
        }
    }

    private void showReceipt(int customerId) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("========================================\n");
        receipt.append("    DIGITAL PHARMACY - SALE RECEIPT\n");
        receipt.append("========================================\n\n");

        String customerName = "Walk-in Customer";
        if (customerId > 0 && cmbCustomer.getSelectedIndex() > 0) {
            customerName = customerList.get(cmbCustomer.getSelectedIndex() - 1).getCustomerName();
        }
        receipt.append("Customer: ").append(customerName).append("\n");
        receipt.append("Payment: ").append(cmbPayment.getSelectedItem()).append("\n");
        receipt.append("Billed By: ").append(currentUser.getFullName()).append("\n\n");
        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-25s %5s %10s\n", "Medicine", "Qty", "Amount"));
        receipt.append("----------------------------------------\n");

        for (SaleItem item : cartItems) {
            receipt.append(String.format("%-25s %5d %10.2f\n",
                    item.getMedicineName().length() > 25 ? item.getMedicineName().substring(0, 22) + "..."
                            : item.getMedicineName(),
                    item.getQuantity(), item.getTotalPrice()));
        }

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("%-25s %5s %10.2f\n", "GRAND TOTAL", "", grandTotal));
        receipt.append("========================================\n");
        receipt.append("       Thank you for your purchase!\n");

        JTextArea txtReceipt = new JTextArea(receipt.toString());
        txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtReceipt.setEditable(false);
        JScrollPane scrollReceipt = new JScrollPane(txtReceipt);
        scrollReceipt.setPreferredSize(new Dimension(420, 350));

        JOptionPane.showMessageDialog(this, scrollReceipt, "Sale Complete - Receipt", JOptionPane.INFORMATION_MESSAGE);
    }
}
