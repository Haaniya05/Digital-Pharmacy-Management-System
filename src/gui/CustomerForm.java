package gui;

import dao.CustomerDAO;
import model.Customer;
import util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * CustomerForm.java - CRUD interface for managing customer records.
 * 
 * Features:
 * - Add, Update, Delete customers
 * - Search by name
 * - JTable listing with click-to-edit
 */
public class CustomerForm extends JFrame {

    private JTextField txtId, txtName, txtPhone, txtEmail;
    private JTextArea txtAddress;
    private JTextField txtSearch;
    private JTable tblCustomers;
    private DefaultTableModel tableModel;
    private CustomerDAO customerDAO = new CustomerDAO();

    public CustomerForm() {
        initComponents();
        loadAllCustomers();
    }

    private void initComponents() {
        setTitle("Customer Management");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(111, 66, 193));
        headerPanel.setPreferredSize(new Dimension(900, 45));
        JLabel lblHeader = new JLabel("Customer Management");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(Color.WHITE);
        headerPanel.add(lblHeader);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        formPanel.setPreferredSize(new Dimension(400, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 5, 6, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(18);
        txtId.setEditable(false);
        txtId.setBackground(new Color(230, 230, 230));
        addRow(formPanel, gc, 0, "Customer ID:", txtId);

        txtName = new JTextField(18);
        addRow(formPanel, gc, 1, "Name: *", txtName);

        txtPhone = new JTextField(18);
        addRow(formPanel, gc, 2, "Phone:", txtPhone);

        txtEmail = new JTextField(18);
        addRow(formPanel, gc, 3, "Email:", txtEmail);

        gc.gridx = 0;
        gc.gridy = 4;
        formPanel.add(new JLabel("Address:"), gc);
        txtAddress = new JTextArea(3, 18);
        txtAddress.setLineWrap(true);
        gc.gridx = 1;
        formPanel.add(new JScrollPane(txtAddress), gc);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        JButton btnAdd = styledBtn("Add", new Color(40, 167, 69));
        JButton btnUpdate = styledBtn("Update", new Color(0, 123, 255));
        JButton btnDelete = styledBtn("Delete", new Color(220, 53, 69));
        JButton btnClear = styledBtn("Clear", new Color(108, 117, 125));
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gc.gridx = 0;
        gc.gridy = 5;
        gc.gridwidth = 2;
        gc.insets = new Insets(15, 5, 5, 5);
        formPanel.add(btnPanel, gc);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search: "));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = styledBtn("Search", new Color(111, 66, 193));
        btnSearch.setPreferredSize(new Dimension(90, 30));
        searchPanel.add(btnSearch);
        JButton btnRefresh = styledBtn("Show All", new Color(108, 117, 125));
        btnRefresh.setPreferredSize(new Dimension(90, 30));
        searchPanel.add(btnRefresh);
        tablePanel.add(searchPanel, BorderLayout.NORTH);

        String[] columns = { "ID", "Name", "Phone", "Email", "Address" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblCustomers = new JTable(tableModel);
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustomers.setRowHeight(25);
        tblCustomers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblCustomers.getTableHeader().setBackground(new Color(111, 66, 193));
        tblCustomers.getTableHeader().setForeground(Color.WHITE);
        tablePanel.add(new JScrollPane(tblCustomers), BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Event Listeners
        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchCustomers());
        btnRefresh.addActionListener(e -> loadAllCustomers());

        tblCustomers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblCustomers.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(s(tableModel.getValueAt(row, 1)));
                    txtPhone.setText(s(tableModel.getValueAt(row, 2)));
                    txtEmail.setText(s(tableModel.getValueAt(row, 3)));
                    txtAddress.setText(s(tableModel.getValueAt(row, 4)));
                }
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    searchCustomers();
            }
        });
    }

    private void addRow(JPanel p, GridBagConstraints gc, int row, String label, JTextField field) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 1;
        gc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(lbl, gc);
        gc.gridx = 1;
        gc.weightx = 1.0;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(field, gc);
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

    private String s(Object o) {
        return o != null ? o.toString() : "";
    }

    private void addCustomer() {
        if (ValidationUtil.isEmpty(txtName.getText(), "Customer Name", this))
            return;

        Customer c = new Customer(txtName.getText().trim(), txtPhone.getText().trim(),
                txtEmail.getText().trim(), txtAddress.getText().trim());
        if (customerDAO.addCustomer(c)) {
            ValidationUtil.showSuccess(this, "Customer added successfully!");
            clearFields();
            loadAllCustomers();
        } else {
            ValidationUtil.showError(this, "Failed to add customer!");
        }
    }

    private void updateCustomer() {
        if (txtId.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a customer first!");
            return;
        }
        if (ValidationUtil.isEmpty(txtName.getText(), "Customer Name", this))
            return;

        Customer c = new Customer(Integer.parseInt(txtId.getText()), txtName.getText().trim(),
                txtPhone.getText().trim(), txtEmail.getText().trim(), txtAddress.getText().trim());
        if (customerDAO.updateCustomer(c)) {
            ValidationUtil.showSuccess(this, "Customer updated!");
            clearFields();
            loadAllCustomers();
        } else {
            ValidationUtil.showError(this, "Failed to update customer!");
        }
    }

    private void deleteCustomer() {
        if (txtId.getText().isEmpty()) {
            ValidationUtil.showError(this, "Select a customer first!");
            return;
        }
        if (ValidationUtil.confirmAction(this, "Delete this customer?")) {
            if (customerDAO.deleteCustomer(Integer.parseInt(txtId.getText()))) {
                ValidationUtil.showSuccess(this, "Customer deleted!");
                clearFields();
                loadAllCustomers();
            } else {
                ValidationUtil.showError(this, "Failed to delete customer!");
            }
        }
    }

    private void searchCustomers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllCustomers();
            return;
        }
        populateTable(customerDAO.searchByName(keyword));
    }

    private void loadAllCustomers() {
        populateTable(customerDAO.getAllCustomers());
    }

    private void populateTable(List<Customer> list) {
        tableModel.setRowCount(0);
        for (Customer c : list) {
            tableModel.addRow(new Object[] { c.getCustomerId(), c.getCustomerName(), c.getPhone(), c.getEmail(),
                    c.getAddress() });
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        tblCustomers.clearSelection();
    }
}
