package com.Dao; 

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Repository
public class UserLoginDao {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public boolean verifyLogin(String email, String plainTextPassword) {
        Connection connection = null;
        CallableStatement cs = null;
        String storedHashedPassword = null;
        boolean loginSuccess = false;

        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            System.out.println("Connection obtained for login verification.");

            String sql = "{CALL get_hashed_password_by_email(?, ?)}";
            cs = connection.prepareCall(sql);

            cs.setString(1, email);
            cs.registerOutParameter(2, Types.LONGVARCHAR);

            System.out.println("Executing login verification for email: " + email);
            cs.execute();

            storedHashedPassword = cs.getString(2);

            if (storedHashedPassword != null && !storedHashedPassword.isEmpty()) {
                System.out.println("Email found in database. Comparing passwords...");
                if (passwordEncoder.matches(plainTextPassword, storedHashedPassword)) {
                    loginSuccess = true;
                    System.out.println("Login successful for: " + email);
                } else {
                    System.out.println("Login failed: Incorrect password for " + email);
                }
            } else {
                System.out.println("Login failed: User with email '" + email + "' not found or no password stored.");
            }

        } catch (SQLException e) {
            System.err.println("Database error during login verification: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Login verification failed due to database error: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("General error during login verification: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Login verification failed: " + e.getMessage(), e);
        } finally {
            try { if (cs != null) cs.close(); } catch (SQLException e) { System.err.println("Error closing CallableStatement: " + e.getMessage()); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); }
            System.out.println("Database resources closed for login verification.");
        }
        return loginSuccess;
    }
    /**
     * Fetches the user's name from the userRegistrationDetails table by email.
     * @param email The email of the user.
     * @return The user's name if found, otherwise null.
     */
    public String getUserNameByEmail(String email) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            String sql = "SELECT name FROM userRegistrationDetails WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
            return null; // Return null if email not found
        } catch (SQLException e) {
            System.err.println("Database error fetching user name by email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching user name: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (connection != null) connection.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // --- EXISTING METHODS FOR FORGOT PASSWORD FLOW ---
    public boolean emailExistsInUsers(String email) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            String sql = "SELECT COUNT(*) FROM userRegistrationDetails WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Database error checking email existence in users table: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error checking email existence: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (connection != null) connection.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Method to save or update OTP in password_resets table
    public void saveOtp(String email, String otp, LocalDateTime expiryTime) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            // Using INSERT ... ON DUPLICATE KEY UPDATE for MySQL (upsert)
            String sql = "INSERT INTO password_resets (email, otp, expiry_time) VALUES (?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE otp = ?, expiry_time = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, otp);
            ps.setString(3, expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))); // Format for DATETIME
            ps.setString(4, otp);
            ps.setString(5, expiryTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.executeUpdate();
            System.out.println("OTP saved/updated for email: " + email);
        } catch (SQLException e) {
            System.err.println("Database error saving OTP: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving OTP: " + e.getMessage(), e);
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (connection != null) connection.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Method to retrieve and verify OTP from password_resets table
    public boolean verifyOtp(String email, String otp) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            String sql = "SELECT otp, expiry_time FROM password_resets WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                String storedOtp = rs.getString("otp");
                // Convert SQL Timestamp to LocalDateTime for comparison
                LocalDateTime expiryTime = rs.getTimestamp("expiry_time").toLocalDateTime();

                if (storedOtp.equals(otp) && LocalDateTime.now().isBefore(expiryTime)) {
                    System.out.println("OTP verified successfully for email: " + email);
                    // Optionally, delete the OTP after successful verification to prevent reuse
                    deleteOtp(email);
                    return true;
                } else {
                    System.out.println("OTP mismatch or expired for email: " + email);
                    return false;
                }
            }
            System.out.println("No OTP record found for email: " + email);
            return false; // No OTP record found for this email
        } catch (SQLException e) {
            System.err.println("Database error verifying OTP: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error verifying OTP: " + e.getMessage(), e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (connection != null) connection.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Method to delete OTP from password_resets table
    public void deleteOtp(String email) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            String sql = "DELETE FROM password_resets WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.executeUpdate();
            System.out.println("OTP deleted for email: " + email);
        } catch (SQLException e) {
            System.err.println("Database error deleting OTP: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error deleting OTP: " + e.getMessage(), e);
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { /* ignore */ }
            try { if (connection != null) connection.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    // Method to update user's password in userRegistrationDetails table
    public boolean updatePassword(String email, String hashedPassword) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = com.Dbconnection.AdminDBconnection.getConnection();
            String sql = "UPDATE userRegistrationDetails SET password = ? WHERE email = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully for email: " + email);
                return true;
            } else {
                System.out.println("Password update failed for email: " + email + " (user not found).");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database error updating password: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error updating password: " + e.getMessage(), e);
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { }
            try { if (connection != null) connection.close(); } catch (SQLException e) { }
        }
    }
}