package model;

/**
 * Sale.java - Model class representing a sales transaction header.
 * 
 * Maps to the 'sales' table. Each sale belongs to a customer (optional for
 * walk-in customers) and records the date, total amount, and payment mode.
 * Individual sold items are stored in SaleItem.
 */
public class Sale {

    private int saleId;
    private int customerId;
    private String customerName; // populated via JOIN
    private java.sql.Date saleDate;
    private double totalAmount;
    private String paymentMode; // "Cash", "Card", or "UPI"
    private int soldBy;
    private String soldByName; // populated via JOIN

    public Sale() {
    }

    public Sale(int customerId, java.sql.Date  saleDate, double totalAmount, String paymentMode, int soldBy) {
        this.customerId = customerId;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.paymentMode = paymentMode;
        this.soldBy = soldBy;
    }

    public Sale(int saleId, int customerId, String customerName, java.sql.Date  saleDate,
            double totalAmount, String paymentMode, int soldBy, String soldByName) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.saleDate = saleDate;
        this.totalAmount = totalAmount;
        this.paymentMode = paymentMode;
        this.soldBy = soldBy;
        this.soldByName = soldByName;
    }

    // ==================== Getters and Setters ====================

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
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

    public java.sql.Date  getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(java.sql.Date  saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public int getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(int soldBy) {
        this.soldBy = soldBy;
    }

    public String getSoldByName() {
        return soldByName;
    }

    public void setSoldByName(String soldByName) {
        this.soldByName = soldByName;
    }

    @Override
    public String toString() {
        return "Sale #" + saleId + " - Rs." + totalAmount + " (" + paymentMode + ")";
    }
}
