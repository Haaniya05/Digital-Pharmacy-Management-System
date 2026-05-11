package dao;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO.java - Data Access Object for User operations.
 * 
 * Handles all database interactions related to the 'users' table including
 * authentication, user creation, and retrieval.
 */
public class UserDAO {

    /**
     * Authenticates a user by matching username and password in the database.
     * 
     * @param username The username entered by the user
     * @param password The password entered by the user
     * @return User object if credentials match, null otherwise
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new user to the database.
     * 
     * @param user The User object to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, role, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPassword());
            pst.setString(3, user.getFullName());
            pst.setString(4, user.getRole());
            pst.setString(5, user.getEmail());
            pst.setString(6, user.getPhone());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error adding user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all users from the database.
     * 
     * @return List of all User objects
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY full_name";
        try (Connection conn = DBConnection.getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error fetching users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Helper method to extract a User object from a ResultSet row.
     */
    private User extractUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("phone"));
    }
}
