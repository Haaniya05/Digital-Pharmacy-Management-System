package model;

/**
 * SaleItem.java - Model for individual line items within a sale.
 * 
 * Maps to the 'sale_items' table. Each item references a medicine and
 * records quantity, unit price, and calculated total price.
 */
public class SaleItem {

    private int itemId;
    private int saleId;
    private int medicineId;
    private String medicineName; // populated via JOIN
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public SaleItem() {
    }

    public SaleItem(int saleId, int medicineId, int quantity, double unitPrice, double totalPrice) {
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    public SaleItem(int itemId, int saleId, int medicineId, String medicineName,
            int quantity, double unitPrice, double totalPrice) {
        this.itemId = itemId;
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // ==================== Getters and Setters ====================

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return medicineName + " x" + quantity + " = Rs." + totalPrice;
    }
}
