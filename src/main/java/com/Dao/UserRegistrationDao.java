package com.Dao; // Adjust package as per your project structure

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.Dbconnection.AdminDBconnection;
import com.dto.UserRegistrationDto; // Corrected package for DTO

@Repository
public class UserRegistrationDao {

    public String userRegistration(UserRegistrationDto dto) {
        Connection connection = null;
        PreparedStatement ps = null;
        String sql = null;
        String status = "failed"; // Default status

        try {
            connection = AdminDBconnection.getConnection();
            System.out.println("Connection obtained for registration.");

            sql = "{CALL insert_user_registration_details(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
            ps = connection.prepareStatement(sql);

            // Set all parameters according to the stored procedure's definition
            ps.setString(1, dto.getName());
            ps.setString(2, dto.getEmail());
            ps.setString(3, dto.getPhone());
            ps.setString(4, dto.getPassword()); // This MUST be the HASHED password from the service layer
            ps.setDate(5, dto.getDob());
            ps.setBigDecimal(6, dto.getBodyWeight());
            ps.setString(7, dto.getHeight());
            ps.setString(8, dto.getPreExistingMedicalCondition());
            ps.setString(9, dto.getExperiencedPastThreeMonths());
            ps.setString(10, dto.getRemark());
            ps.setString(11, dto.getGender());
            ps.setString(12, dto.getEmergencyNo());
            ps.setString(13, dto.getAddress());
            ps.setString(14, dto.getCity());

            System.out.println("Executing registration statement...");
            int rowsAffected = ps.executeUpdate(); // Use executeUpdate() for INSERT

            if (rowsAffected > 0) {
                status = "success";
                System.out.println("User registered successfully. Rows affected: " + rowsAffected);
            } else {
                status = "failed: No rows affected";
                System.out.println("User registration failed: No rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
            // Specific handling for duplicate entry error (MySQL error code 1062)
            if (e.getErrorCode() == 1062) {
                 throw new RuntimeException("Registration failed: This email or name is already registered.", e);
            }
            throw new RuntimeException("Registration failed due to database error: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("General error during registration: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); }
            try { if (connection != null) connection.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); }
            System.out.println("Database resources closed for registration.");
        }
        return status;
    }
}