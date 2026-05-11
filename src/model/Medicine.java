package model;

/**
 * Medicine.java - Model class representing a medicine in the pharmacy
 * inventory.
 * 
 * This POJO maps directly to the 'medicines' table in the database.
 * It stores all details about a medicine including stock, pricing, and expiry
 * info.
 */
public class Medicine {

    private int medicineId;
    private String medicineName;
    private String genericName;
    private String category;
    private String batchNo;
    private double price;
    private int quantity;
    private String mfgDate; // stored as String for easy display (yyyy-MM-dd)
    private String expDate;
    private String supplier;
    private String description;

    // Default constructor
    public Medicine() {
    }

    // Constructor without ID (for adding new medicine)
    public Medicine(String medicineName, String genericName, String category, String batchNo,
            double price, int quantity, String mfgDate, String expDate, String supplier, String description) {
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.category = category;
        this.batchNo = batchNo;
        this.price = price;
        this.quantity = quantity;
        this.mfgDate = mfgDate;
        this.expDate = expDate;
        this.supplier = supplier;
        this.description = description;
    }

    // Full constructor (with ID)
    public Medicine(int medicineId, String medicineName, String genericName, String category, String batchNo,
            double price, int quantity, String mfgDate, String expDate, String supplier, String description) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.genericName = genericName;
        this.category = category;
        this.batchNo = batchNo;
        this.price = price;
        this.quantity = quantity;
        this.mfgDate = mfgDate;
        this.expDate = expDate;
        this.supplier = supplier;
        this.description = description;
    }

    // ==================== Getters and Setters ====================

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

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMfgDate() {
        return mfgDate;
    }

    public void setMfgDate(String mfgDate) {
        this.mfgDate = mfgDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return medicineName + " (" + genericName + ") - Qty: " + quantity;
    }
}
