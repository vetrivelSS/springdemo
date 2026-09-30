package com.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import com.Dbconnection.AdminDBconnection;
@Repository
public class AdminDao {
	 public boolean validateAdmin(String email, String password) {
	        boolean isValid = false;

	        try (Connection con = AdminDBconnection.getConnection()) {
	            String query = "SELECT * FROM admindetails WHERE email_id = ? AND password = ?";
	            PreparedStatement ps = con.prepareStatement(query);
	            ps.setString(1, email);
	            ps.setString(2, password);
	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	                isValid = true;
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return isValid;
	    }

}
