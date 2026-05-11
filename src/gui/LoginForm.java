package gui;

import dao.UserDAO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.User;

/**
 * LoginForm.java - The entry point of the application.
 * 
 * Displays a login window with username and password fields.
 * Authenticates against the database using UserDAO.
 * On successful login, opens the DashboardForm and closes this window.
 */
public class LoginForm extends JFrame {

    // UI Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JLabel lblStatus;

    // DAO
    private UserDAO userDAO = new UserDAO();

    /**
     * Constructor - Sets up the login form UI.
     */
    public LoginForm() {
        initComponents();
    }

    /**
     * Initializes and arranges all UI components.
     */
    private void initComponents() {
        // ==================== Frame Settings ====================
        setTitle("Digital Pharmacy Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // ==================== Main Panel with gradient-like background
        // ====================
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // AliceBlue

        // ==================== Title Panel ====================
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 153)); // Dark teal
        titlePanel.setPreferredSize(new Dimension(500, 80));
        titlePanel.setLayout(new GridBagLayout());

        JLabel lblTitle = new JLabel("DIGITAL PHARMACY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Management System");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(200, 230, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        titlePanel.add(lblTitle, gbc);
        gbc.gridy = 1;
        titlePanel.add(lblSubtitle, gbc);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // ==================== Login Form Panel ====================
        JPanel formPanel = new JPanel();
        formPanel.setBackground(new Color(240, 248, 255));
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(10, 10, 10, 10);

        // Username label
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fc.gridx = 0;
        fc.gridy = 0;
        fc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblUsername, fc);

        // Username text field
        txtUsername = new JTextField(18);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(220, 35));
        fc.gridx = 1;
        fc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtUsername, fc);

        // Password label
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fc.gridx = 0;
        fc.gridy = 1;
        fc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblPassword, fc);

        // Password field
        txtPassword = new JPasswordField(18);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(220, 35));
        fc.gridx = 1;
        fc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtPassword, fc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(240, 248, 255));

        btnLogin = new JButton("  Login  ");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(0, 102, 153));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(120, 40));

        btnClear = new JButton("  Clear  ");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClear.setPreferredSize(new Dimension(120, 40));
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);

        fc.gridx = 0;
        fc.gridy = 2;
        fc.gridwidth = 2;
        fc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, fc);

        // Status label (for error messages)
        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.RED);
        fc.gridy = 3;
        formPanel.add(lblStatus, fc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ==================== Footer Panel ====================
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(0, 102, 153));
        footerPanel.setPreferredSize(new Dimension(500, 35));
        JLabel lblFooter = new JLabel("© 2026 Digital Pharmacy Management System");
        lblFooter.setForeground(new Color(200, 230, 255));
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerPanel.add(lblFooter);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ==================== Event Listeners ====================
        btnLogin.addActionListener(e -> performLogin());
        btnClear.addActionListener(e -> clearFields());

        // Allow Enter key to trigger login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }

    /**
     * Validates credentials and opens the Dashboard on success.
     */
    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both username and password!");
            return;
        }

        lblStatus.setText("Authenticating...");
        lblStatus.setForeground(new Color(0, 102, 153));

        // Authenticate using DAO
        User user = userDAO.authenticate(username, password);

        if (user != null) {
            lblStatus.setText("Login successful! Welcome, " + user.getFullName());
            lblStatus.setForeground(new Color(0, 128, 0)); // Green

            // Open Dashboard and close Login
            SwingUtilities.invokeLater(() -> {
                DashboardForm dashboard = new DashboardForm(user);
                dashboard.setVisible(true);
                dispose(); // Close login window
            });
        } else {
            lblStatus.setText("Invalid username or password!");
            lblStatus.setForeground(Color.RED);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    /**
     * Clears all input fields and status label.
     */
    private void clearFields() {
        txtUsername.setText("");
        txtPassword.setText("");
        lblStatus.setText(" ");
        txtUsername.requestFocus();
    }

    /**
     * Application entry point - launches the login form.
     */
    public static void main(String[] args) {
        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
