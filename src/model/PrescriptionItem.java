package model;

/**
 * PrescriptionItem.java - Model for individual line items within a
 * prescription.
 * 
 * Maps to the 'prescription_items' table. Each item references a medicine
 * and specifies quantity and dosage instructions.
 */
public class PrescriptionItem {

    private int itemId;
    private int prescriptionId;
    private int medicineId;
    private String medicineName; // populated via JOIN
    private int quantity;
    private String dosage;

    public PrescriptionItem() {
    }

    public PrescriptionItem(int prescriptionId, int medicineId, int quantity, String dosage) {
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.dosage = dosage;
    }

    public PrescriptionItem(int itemId, int prescriptionId, int medicineId,
            String medicineName, int quantity, String dosage) {
        this.itemId = itemId;
        this.prescriptionId = prescriptionId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dosage = dosage;
    }

    // ==================== Getters and Setters ====================

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    @Override
    public String toString() {
        return medicineName + " x" + quantity + " (" + dosage + ")";
    }
}
