package model;

/**
 * Customer.java - Model class representing a pharmacy customer.
 * 
 * This POJO maps directly to the 'customers' table in the database.
 * Stores customer personal and contact information.
 */
public class Customer {

    private int customerId;
    private String customerName;
    private String phone;
    private String email;
    private String address;

    public Customer() {
    }

    public Customer(String customerName, String phone, String email, String address) {
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public Customer(int customerId, String customerName, String phone, String email, String address) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // ==================== Getters and Setters ====================

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return customerName + " (" + phone + ")";
    }
}
