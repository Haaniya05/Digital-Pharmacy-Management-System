package gui;

import dao.CustomerDAO;
import dao.MedicineDAO;
import dao.PrescriptionDAO;
import model.Customer;
import model.Medicine;
import model.Prescription;
import model.PrescriptionItem;
import util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PrescriptionForm.java - Interface for creating and viewing prescriptions.
 * 
 * Features:
 * - Create new prescriptions with customer, doctor, and medicine items
 * - Add multiple medicines to a single prescription
 * - View existing prescriptions and their items
 * - Transactional save (all-or-nothing)
 */
public class PrescriptionForm extends JFrame {

    // Input fields
    private JComboBox<String> cmbCustomer;
    private JTextField txtDoctor, txtDate;
    private JTextArea txtNotes;
    private JComboBox<String> cmbMedicine;
    private JTextField txtQty, txtDosage;

    // Tables
    private JTable tblItems, tblPrescriptions;
    private DefaultTableModel modelItems, modelPrescriptions;

    // Data lists for combo box mapping
    private List<Customer> customerList;
    private List<Medicine> medicineList;

    // DAOs
    private CustomerDAO customerDAO = new CustomerDAO();
    private MedicineDAO medicineDAO = new MedicineDAO();
    private PrescriptionDAO prescriptionDAO = new PrescriptionDAO();

    // Prescription items being built
    private List<PrescriptionItem> currentItems = new ArrayList<>();

    public PrescriptionForm() {
        initComponents();
        loadComboBoxData();
        loadPrescriptions();
    }

    private void initComponents() {
        setTitle("Prescription Management");
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(23, 162, 184));
        headerPanel.setPreferredSize(new Dimension(1050, 45));
        JLabel lblHeader = new JLabel("Prescription Management");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== Left: New Prescription Form ====================
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setPreferredSize(new Dimension(420, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Prescription header fields
        JPanel headerFields = new JPanel(new GridBagLayout());
        headerFields.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNewRx = new JLabel("New Prescription");
        lblNewRx.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        headerFields.add(lblNewRx, gc);
        gc.gridwidth = 1;

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0;
        headerFields.add(new JLabel("Customer: *"), gc);
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(cmbCustomer, gc);

        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 0;
        headerFields.add(new JLabel("Doctor: *"), gc);
        txtDoctor = new JTextField(18);
        txtDoctor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(txtDoctor, gc);

        gc.gridx = 0;
        gc.gridy = 3;
        gc.weightx = 0;
        headerFields.add(new JLabel("Date (YYYY-MM-DD): *"), gc);
        txtDate = new JTextField(18);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(txtDate, gc);

        gc.gridx = 0;
        gc.gridy = 4;
        gc.weightx = 0;
        headerFields.add(new JLabel("Notes:"), gc);
        txtNotes = new JTextArea(2, 18);
        txtNotes.setLineWrap(true);
        txtNotes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(new JScrollPane(txtNotes), gc);

        // Separator
        gc.gridx = 0;
        gc.gridy = 5;
        gc.gridwidth = 2;
        headerFields.add(new JSeparator(), gc);
        gc.gridwidth = 1;

        // Add medicine item section
        JLabel lblAddItem = new JLabel("Add Medicine Item");
        lblAddItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gc.gridx = 0;
        gc.gridy = 6;
        gc.gridwidth = 2;
        headerFields.add(lblAddItem, gc);
        gc.gridwidth = 1;

        gc.gridx = 0;
        gc.gridy = 7;
        gc.weightx = 0;
        headerFields.add(new JLabel("Medicine:"), gc);
        cmbMedicine = new JComboBox<>();
        cmbMedicine.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(cmbMedicine, gc);

        gc.gridx = 0;
        gc.gridy = 8;
        gc.weightx = 0;
        headerFields.add(new JLabel("Quantity:"), gc);
        txtQty = new JTextField(18);
        txtQty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtQty.setText("1");
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(txtQty, gc);

        gc.gridx = 0;
        gc.gridy = 9;
        gc.weightx = 0;
        headerFields.add(new JLabel("Dosage:"), gc);
        txtDosage = new JTextField(18);
        txtDosage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDosage.setText("1 tablet twice daily");
        gc.gridx = 1;
        gc.weightx = 1.0;
        headerFields.add(txtDosage, gc);

        JButton btnAddItem = styledBtn("+ Add Item", new Color(0, 123, 255));
        gc.gridx = 0;
        gc.gridy = 10;
        gc.gridwidth = 2;
        headerFields.add(btnAddItem, gc);

        leftPanel.add(headerFields, BorderLayout.NORTH);

        // Items table (items being added to current prescription)
        String[] itemCols = { "Medicine", "Qty", "Dosage" };
        modelItems = new DefaultTableModel(itemCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblItems = new JTable(modelItems);
        tblItems.setRowHeight(22);
        leftPanel.add(new JScrollPane(tblItems), BorderLayout.CENTER);

        // Save and Clear buttons
        JPanel saveBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        saveBtnPanel.setOpaque(false);
        JButton btnSave = styledBtn("Save Prescription", new Color(40, 167, 69));
        JButton btnClear = styledBtn("Clear All", new Color(108, 117, 125));
        JButton btnRemoveItem = styledBtn("Remove Item", new Color(220, 53, 69));
        saveBtnPanel.add(btnSave);
        saveBtnPanel.add(btnRemoveItem);
        saveBtnPanel.add(btnClear);
        leftPanel.add(saveBtnPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // ==================== Right: Existing Prescriptions ====================
        JPanel rightPanel = new JPanel(new BorderLayout(0, 8));
        rightPanel.setOpaque(false);

        JLabel lblExisting = new JLabel("  Existing Prescriptions");
        lblExisting.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightPanel.add(lblExisting, BorderLayout.NORTH);

        String[] rxCols = { "ID", "Customer", "Doctor", "Date", "Notes" };
        modelPrescriptions = new DefaultTableModel(rxCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblPrescriptions = new JTable(modelPrescriptions);
        tblPrescriptions.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblPrescriptions.setRowHeight(25);
        tblPrescriptions.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblPrescriptions.getTableHeader().setBackground(new Color(23, 162, 184));
        tblPrescriptions.getTableHeader().setForeground(Color.WHITE);
        rightPanel.add(new JScrollPane(tblPrescriptions), BorderLayout.CENTER);

        // View items button
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewPanel.setOpaque(false);
        JButton btnViewItems = styledBtn("View Items for Selected", new Color(23, 162, 184));
        JButton btnRefresh = styledBtn("Refresh", new Color(108, 117, 125));
        viewPanel.add(btnViewItems);
        viewPanel.add(btnRefresh);
        rightPanel.add(viewPanel, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // ==================== Event Listeners ====================
        btnAddItem.addActionListener(e -> addItemToList());
        btnRemoveItem.addActionListener(e -> removeItemFromList());
        btnSave.addActionListener(e -> savePrescription());
        btnClear.addActionListener(e -> clearAll());
        btnViewItems.addActionListener(e -> viewPrescriptionItems());
        btnRefresh.addActionListener(e -> loadPrescriptions());
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

    private void loadComboBoxData() {
        customerList = customerDAO.getAllCustomers();
        cmbCustomer.removeAllItems();
        for (Customer c : customerList) {
            cmbCustomer.addItem(c.getCustomerId() + " - " + c.getCustomerName());
        }

        medicineList = medicineDAO.getAllMedicines();
        cmbMedicine.removeAllItems();
        for (Medicine m : medicineList) {
            cmbMedicine.addItem(m.getMedicineId() + " - " + m.getMedicineName() + " (Qty: " + m.getQuantity() + ")");
        }
    }

    private void addItemToList() {
        if (cmbMedicine.getSelectedIndex() < 0) {
            ValidationUtil.showError(this, "Select a medicine!");
            return;
        }
        if (ValidationUtil.isNotValidInteger(txtQty.getText(), "Quantity", this))
            return;

        Medicine selected = medicineList.get(cmbMedicine.getSelectedIndex());
        int qty = Integer.parseInt(txtQty.getText().trim());

        PrescriptionItem item = new PrescriptionItem();
        item.setMedicineId(selected.getMedicineId());
        item.setMedicineName(selected.getMedicineName());
        item.setQuantity(qty);
        item.setDosage(txtDosage.getText().trim());
        currentItems.add(item);

        modelItems.addRow(new Object[] { selected.getMedicineName(), qty, txtDosage.getText().trim() });
        txtQty.setText("1");
        txtDosage.setText("1 tablet twice daily");
    }

    private void removeItemFromList() {
        int row = tblItems.getSelectedRow();
        if (row >= 0) {
            currentItems.remove(row);
            modelItems.removeRow(row);
        } else {
            ValidationUtil.showError(this, "Select an item to remove!");
        }
    }

    private void savePrescription() {
        if (cmbCustomer.getSelectedIndex() < 0) {
            ValidationUtil.showError(this, "Select a customer!");
            return;
        }
        if (ValidationUtil.isEmpty(txtDoctor.getText(), "Doctor Name", this))
            return;
        if (ValidationUtil.isNotValidDate(txtDate.getText(), "Prescription Date", this))
            return;
        if (currentItems.isEmpty()) {
            ValidationUtil.showError(this, "Add at least one medicine item!");
            return;
        }

        Customer selectedCustomer = customerList.get(cmbCustomer.getSelectedIndex());
        Prescription rx = new Prescription(
                selectedCustomer.getCustomerId(),
                txtDoctor.getText().trim(),
                txtDate.getText().trim(),
                txtNotes.getText().trim());

        if (prescriptionDAO.addPrescription(rx, currentItems)) {
            ValidationUtil.showSuccess(this, "Prescription saved successfully!");
            clearAll();
            loadPrescriptions();
        } else {
            ValidationUtil.showError(this, "Failed to save prescription!");
        }
    }

    private void loadPrescriptions() {
        modelPrescriptions.setRowCount(0);
        List<Prescription> list = prescriptionDAO.getAllPrescriptions();
        for (Prescription p : list) {
            modelPrescriptions.addRow(new Object[] {
                    p.getPrescriptionId(), p.getCustomerName(), p.getDoctorName(),
                    p.getPrescriptionDate(), p.getNotes()
            });
        }
    }

    private void viewPrescriptionItems() {
        int row = tblPrescriptions.getSelectedRow();
        if (row < 0) {
            ValidationUtil.showError(this, "Select a prescription first!");
            return;
        }

        int rxId = (int) modelPrescriptions.getValueAt(row, 0);
        List<PrescriptionItem> items = prescriptionDAO.getItemsByPrescriptionId(rxId);

        StringBuilder sb = new StringBuilder("Prescription #" + rxId + " Items:\n\n");
        for (PrescriptionItem item : items) {
            sb.append("• ").append(item.getMedicineName())
                    .append(" (Qty: ").append(item.getQuantity())
                    .append(", Dosage: ").append(item.getDosage()).append(")\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Prescription Items", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearAll() {
        txtDoctor.setText("");
        txtDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        txtNotes.setText("");
        txtQty.setText("1");
        txtDosage.setText("1 tablet twice daily");
        currentItems.clear();
        modelItems.setRowCount(0);
    }
}
