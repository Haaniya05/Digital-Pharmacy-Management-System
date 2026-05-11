package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * TableUtil.java - Helper class for populating JTable components.
 * 
 * Provides a reusable method to fill a JTable with data from any List of Object
 * arrays.
 * This avoids repetitive table-population code across all GUI forms.
 */
public class TableUtil {

    /**
     * Populates a JTable with the given data and column headers.
     * Creates a non-editable DefaultTableModel to prevent users from
     * accidentally modifying data directly in the table.
     * 
     * @param table   The JTable component to populate
     * @param columns Array of column header names
     * @param data    List of Object arrays, each representing a row
     */
    public static void populateTable(JTable table, String[] columns, List<Object[]> data) {
        // Create a non-editable table model
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent direct cell editing
            }
        };

        // Add each row of data to the model
        for (Object[] row : data) {
            model.addRow(row);
        }

        table.setModel(model);
    }

    /**
     * Clears all rows from a JTable while keeping the column headers.
     * 
     * @param table The JTable to clear
     */
    public static void clearTable(JTable table) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
    }
}
