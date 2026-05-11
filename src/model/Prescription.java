package model;

/**
 * Prescription.java - Model class representing a prescription record.
 * 
 * This POJO maps to the 'prescriptions' table. Each prescription belongs to a
 * customer and contains a doctor name, date, and optional notes.
 * Prescription line items are stored separately in PrescriptionItem.
 */
public class Prescription {

    private int prescriptionId;
    private int customerId;
    private String customerName; // populated via JOIN (not stored in DB directly)
    private String doctorName;
    private String prescriptionDate;
    private String notes;

    public Prescription() {
    }

    public Prescription(int customerId, String doctorName, String prescriptionDate, String notes) {
        this.customerId = customerId;
        this.doctorName = doctorName;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
    }

    public Prescription(int prescriptionId, int customerId, String customerName,
            String doctorName, String prescriptionDate, String notes) {
        this.prescriptionId = prescriptionId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.doctorName = doctorName;
        this.prescriptionDate = prescriptionDate;
        this.notes = notes;
    }

    // ==================== Getters and Setters ====================

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(String prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Prescription #" + prescriptionId + " - Dr. " + doctorName;
    }
}
