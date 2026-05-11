package util;

import javax.swing.*;

/**
 * ValidationUtil.java - Input validation helper class.
 * 
 * Provides static methods for common validation checks used across all GUI
 * forms.
 * Displays JOptionPane error dialogs when validation fails, making it easy for
 * any form to validate user input with a single method call.
 */
public class ValidationUtil {

    /**
     * Checks if a string is empty or null.
     * 
     * @param value     The string to validate
     * @param fieldName Name of the field (for error message)
     * @param parent    Parent component for dialog positioning
     * @return true if the field is empty (validation FAILED), false if it has
     *         content
     */
    public static boolean isEmpty(String value, String fieldName, JFrame parent) {
        if (value == null || value.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    fieldName + " cannot be empty!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Checks if a string represents a valid positive number (integer or decimal).
     * 
     * @param value     The string to validate
     * @param fieldName Name of the field (for error message)
     * @param parent    Parent component for dialog positioning
     * @return true if invalid (validation FAILED), false if valid
     */
    public static boolean isNotValidNumber(String value, String fieldName, JFrame parent) {
        try {
            double num = Double.parseDouble(value.trim());
            if (num < 0) {
                JOptionPane.showMessageDialog(parent,
                        fieldName + " must be a positive number!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    fieldName + " must be a valid number!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return true;
        }
    }

    /**
     * Checks if a string represents a valid integer (whole number).
     * 
     * @param value     The string to validate
     * @param fieldName Name of the field (for error message)
     * @param parent    Parent component for dialog positioning
     * @return true if invalid (validation FAILED), false if valid
     */
    public static boolean isNotValidInteger(String value, String fieldName, JFrame parent) {
        try {
            int num = Integer.parseInt(value.trim());
            if (num < 0) {
                JOptionPane.showMessageDialog(parent,
                        fieldName + " must be a positive whole number!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parent,
                    fieldName + " must be a valid whole number!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return true;
        }
    }

    /**
     * Validates date format (basic check for yyyy-MM-dd pattern).
     * 
     * @param value     The date string to validate
     * @param fieldName Name of the field (for error message)
     * @param parent    Parent component for dialog positioning
     * @return true if invalid (validation FAILED), false if valid
     */
    public static boolean isNotValidDate(String value, String fieldName, JFrame parent) {
        if (value == null || !value.trim().matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(parent,
                    fieldName + " must be in yyyy-MM-dd format! (e.g., 2025-12-31)",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Validates phone number format (10-15 digits).
     * 
     * @param value     The phone string to validate
     * @param fieldName Name of the field (for error message)
     * @param parent    Parent component for dialog positioning
     * @return true if invalid (validation FAILED), false if valid
     */
    public static boolean isNotValidPhone(String value, String fieldName, JFrame parent) {
        if (value != null && !value.trim().isEmpty() && !value.trim().matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(parent,
                    fieldName + " must be 10-15 digits!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    /**
     * Shows a success message dialog.
     */
    public static void showSuccess(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows an error message dialog.
     */
    public static void showError(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a confirmation dialog (Yes/No).
     * 
     * @return true if user clicked Yes
     */
    public static boolean confirmAction(JFrame parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
