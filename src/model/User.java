package model;

/**
 * User.java - Model class representing a system user (Admin or Pharmacist).
 * 
 * This POJO maps directly to the 'users' table in the database.
 * It stores login credentials and user profile information.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;       // "Admin" or "Pharmacist"
    private String email;
    private String phone;

    // Default constructor
    public User() {}

    // Parameterized constructor (without ID - for new user creation)
    public User(String username, String password, String fullName, String role, String email, String phone) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    // Full constructor (with ID - for fetching from DB)
    public User(int userId, String username, String password, String fullName, String role, String email, String phone) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    // ==================== Getters and Setters ====================

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
