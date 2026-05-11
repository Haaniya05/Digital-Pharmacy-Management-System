package gui;

import dao.MedicineDAO;
import model.Medicine;
import util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MedicineForm.java - Full CRUD interface for managing medicines.
 * 
 * Features:
 * - Add, Update, Delete medicines
 * - Search by name or generic name
 * - JTable listing with click-to-edit
 * - Input validation for all fields
 */
public class MedicineForm extends JFrame {

    // Input fields
    private JTextField txtId, txtName, txtGeneric, txtCategory, txtBatchNo;
    private JTextField txtPrice, txtQuantity, txtMfgDate, txtExpDate, txtSupplier;
    private JTextArea txtDescription;
    private JTextField txtSearch;

    // Table
    private JTable tblMedicines;
    private DefaultTableModel tableModel;

    // DAO
    private MedicineDAO medicineDAO = new MedicineDAO();

    public MedicineForm() {
        initComponents();
        loadAllMedicines();
    }

    private void initComponents() {
        setTitle("Medicine Management");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // ==================== Header ====================
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 153));
        headerPanel.setPreferredSize(new Dimension(1050, 45));
        JLabel lblHeader = new JLabel("Medicine Management");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== Form Panel (Left Side) ====================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        formPanel.setPreferredSize(new Dimension(420, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 5, 4, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // ID (read-only)
        txtId = new JTextField(18);
        txtId.setEditable(false);
        txtId.setBackground(new Color(230, 230, 230));
        addFormRow(formPanel, gc, 0, "Medicine ID:", txtId);

        // Name
        txtName = new JTextField(18);
        addFormRow(formPanel, gc, 1, "Medicine Name: *", txtName);

        // Generic Name
        txtGeneric = new JTextField(18);
        addFormRow(formPanel, gc, 2, "Generic Name:", txtGeneric);

        // Category
        txtCategory = new JTextField(18);
        addFormRow(formPanel, gc, 3, "Category:", txtCategory);

        // Batch No
        txtBatchNo = new JTextField(18);
        addFormRow(formPanel, gc, 4, "Batch No:", txtBatchNo);

        // Price
        txtPrice = new JTextField(18);
        addFormRow(formPanel, gc, 5, "Price (Rs.): *", txtPrice);

        // Quantity
        txtQuantity = new JTextField(18);
        addFormRow(formPanel, gc, 6, "Quantity: *", txtQuantity);

        // Mfg Date
        txtMfgDate = new JTextField(18);
        addFormRow(formPanel, gc, 7, "Mfg Date (YYYY-MM-DD):", txtMfgDate);

        // Exp Date
        txtExpDate = new JTextField(18);
        addFormRow(formPanel, gc, 8, "Exp Date (YYYY-MM-DD):", txtExpDate);

        // Supplier
        txtSupplier = new JTextField(18);
        addFormRow(formPanel, gc, 9, "Supplier:", txtSupplier);

        // Description
        txtDescription = new JTextArea(3, 18);
        txtDescription.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        gc.gridx = 0;
        gc.gridy = 10;
        formPanel.add(new JLabel("Description:"), gc);
        gc.gridx = 1;
        formPanel.add(descScroll, gc);

        // ==================== Buttons Panel ====================
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        JButton btnAdd = createStyledButton("Add", new Color(40, 167, 69));
        JButton btnUpdate = createStyledButton("Update", new Color(0, 123, 255));
        JButton btnDelete = createStyledButton("Delete", new Color(220, 53, 69));
        JButton btnClear = createStyledButton("Clear", new Color(108, 117, 125));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gc.gridx = 0;
        gc.gridy = 11;
        gc.gridwidth = 2;
        gc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // ==================== Table Panel (Right Side) ====================
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setOpaque(false);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search: "));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = createStyledButton("Search", new Color(0, 102, 153));
        btnSearch.setPreferredSize(new Dimension(90, 30));
        searchPanel.add(btnSearch);
        JButton btnRefresh = createStyledButton("Show All", new Color(108, 117, 125));
        btnRefresh.setPreferredSize(new Dimension(90, 30));
        searchPanel.add(btnRefresh);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Name", "Generic", "Category", "Batch", "Price", "Qty", "Mfg Date", "Exp Date",
                "Supplier" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblMedicines = new JTable(tableModel);
        tblMedicines.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblMedicines.setRowHeight(25);
        tblMedicines.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMedicines.getTableHeader().setBackground(new Color(0, 102, 153));
        tblMedicines.getTableHeader().setForeground(Color.WHITE);
        tblMedicines.setSelectionBackground(new Color(0, 102, 153, 50));

        JScrollPane scrollPane = new JScrollPane(tblMedicines);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // ==================== Event Listeners ====================
        btnAdd.addActionListener(e -> addMedicine());
        btnUpdate.addActionListener(e -> updateMedicine());
        btnDelete.addActionListener(e -> deleteMedicine());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchMedicines());
        btnRefresh.addActionListener(e -> loadAllMedicines());

        // Click table row to populate form fields
        tblMedicines.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblMedicines.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtGeneric.setText(safeString(tableModel.getValueAt(row, 2)));
                    txtCategory.setText(safeString(tableModel.getValueAt(row, 3)));
                    txtBatchNo.setText(safeString(tableModel.getValueAt(row, 4)));
                    txtPrice.setText(tableModel.getValueAt(row, 5).toString());
                    txtQuantity.setText(tableModel.getValueAt(row, 6).toString());
                    txtMfgDate.setText(safeString(tableModel.getValueAt(row, 7)));
                    txtExpDate.setText(safeString(tableModel.getValueAt(row, 8)));
                    txtSupplier.setText(safeString(tableModel.getValueAt(row, 9)));
                }
            }
        });

        // Enter key search
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    searchMedicines();
            }
        });
    }

    // ==================== Helper Methods ====================

    private void addFormRow(JPanel panel, GridBagConstraints gc, int row, String label, JTextField field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 1;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        gc.anchor = GridBagConstraints.WEST;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gc);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String safeString(Object val) {
        return val != null ? val.toString() : "";
    }

    // ==================== CRUD Operations ====================

    private void addMedicine() {
        if (ValidationUtil.isEmpty(txtName.getText(), "Medicine Name", this))
            return;
        if (ValidationUtil.isEmpty(txtPrice.getText(), "Price", this))
            return;
        if (ValidationUtil.isNotValidNumber(txtPrice.getText(), "Price", this))
            return;
        if (ValidationUtil.isEmpty(txtQuantity.getText(), "Quantity", this))
            return;
        if (ValidationUtil.isNotValidInteger(txtQuantity.getText(), "Quantity", this))
            return;

        Medicine med = new Medicine(
                txtName.getText().trim(), txtGeneric.getText().trim(), txtCategory.getText().trim(),
                txtBatchNo.getText().trim(), Double.parseDouble(txtPrice.getText().trim()),
                Integer.parseInt(txtQuantity.getText().trim()), txtMfgDate.getText().trim(),
                txtExpDate.getText().trim(), txtSupplier.getText().trim(), txtDescription.getText().trim());

        if (medicineDAO.addMedicine(med)) {
            ValidationUtil.showSuccess(this, "Medicine added successfully!");
            clearFields();
            loadAllMedicines();
        } else {
            ValidationUtil.showError(this, "Failed to add medicine!");
        }
    }

    private void updateMedicine() {
        if (txtId.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a medicine from the table first!");
            return;
        }
        if (ValidationUtil.isEmpty(txtName.getText(), "Medicine Name", this))
            return;
        if (ValidationUtil.isNotValidNumber(txtPrice.getText(), "Price", this))
            return;
        if (ValidationUtil.isNotValidInteger(txtQuantity.getText(), "Quantity", this))
            return;

        Medicine med = new Medicine(
                Integer.parseInt(txtId.getText()), txtName.getText().trim(), txtGeneric.getText().trim(),
                txtCategory.getText().trim(), txtBatchNo.getText().trim(),
                Double.parseDouble(txtPrice.getText().trim()), Integer.parseInt(txtQuantity.getText().trim()),
                txtMfgDate.getText().trim(), txtExpDate.getText().trim(),
                txtSupplier.getText().trim(), txtDescription.getText().trim());

        if (medicineDAO.updateMedicine(med)) {
            ValidationUtil.showSuccess(this, "Medicine updated successfully!");
            clearFields();
            loadAllMedicines();
        } else {
            ValidationUtil.showError(this, "Failed to update medicine!");
        }
    }

    private void deleteMedicine() {
        if (txtId.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a medicine from the table first!");
            return;
        }
        if (ValidationUtil.confirmAction(this, "Are you sure you want to delete this medicine?")) {
            if (medicineDAO.deleteMedicine(Integer.parseInt(txtId.getText()))) {
                ValidationUtil.showSuccess(this, "Medicine deleted successfully!");
                clearFields();
                loadAllMedicines();
            } else {
                ValidationUtil.showError(this, "Failed to delete medicine!");
            }
        }
    }

    private void searchMedicines() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMedicines();
            return;
        }
        List<Medicine> results = medicineDAO.searchByName(keyword);
        populateTable(results);
    }

    private void loadAllMedicines() {
        List<Medicine> medicines = medicineDAO.getAllMedicines();
        populateTable(medicines);
    }

    private void populateTable(List<Medicine> medicines) {
        tableModel.setRowCount(0);
        for (Medicine m : medicines) {
            tableModel.addRow(new Object[] {
                    m.getMedicineId(), m.getMedicineName(), m.getGenericName(), m.getCategory(),
                    m.getBatchNo(), m.getPrice(), m.getQuantity(), m.getMfgDate(),
                    m.getExpDate(), m.getSupplier()
            });
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtGeneric.setText("");
        txtCategory.setText("");
        txtBatchNo.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtMfgDate.setText("");
        txtExpDate.setText("");
        txtSupplier.setText("");
        txtDescription.setText("");
        tblMedicines.clearSelection();
    }
}
