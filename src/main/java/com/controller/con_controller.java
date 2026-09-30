package com.controller;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import com.Dbconnection.AdminDBconnection;

import jakarta.mail.internet.MimeMessage;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class con_controller {

    private final JavaMailSender mailSender;
    private final Map<String, String> otpStore = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    public con_controller(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


@PostMapping("/send-otp")
public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    if (email == null || email.isEmpty()) {
        return ResponseEntity.badRequest().body("Email is required");
    }

    String otp = String.format("%06d", random.nextInt(1_000_000));
    otpStore.put(email, otp);

    try {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("Your One-Time Password (OTP) for Verification");

        // Customize recipient name or fallback to email prefix
        String recipientName = email.split("@")[0]; 

        // HTML email content
        String htmlMsg = "<html><body>" +
            "<div style='font-family:Arial,sans-serif; max-width:600px; margin:auto; padding:20px; border:1px solid #ddd;'>" +
            "" +
            "<h2 style='color:#333;'>Dear " + recipientName + ",</h2>" +
            "<p>Your One-Time Password (OTP) for verification is:</p>" +
            "<h1 style='color:#007bff;'>🔐 OTP: " + otp + "</h1>" +
            "<p>This OTP is valid for <strong>1 minute</strong>.</p>" +
            "<img src='cid:bannerImage' alt='Verification Banner' style='width:70%; max-width:560px; margin:20px 0;'/>" +
            "<p>Best regards,<br><strong>Elevar Technology Team</strong></p>" +
            "</div>" +
            "</body></html>";

        helper.setText(htmlMsg, true);

        // Attach logo image from resources (classpath)
        //helper.addInline("logoImage", new ClassPathResource("static/images/logo.png"));

        // Attach banner image from resources (optional)
       helper.addInline("bannerImage", new ClassPathResource("static/images/logo.png"));

        mailSender.send(message);

        return ResponseEntity.ok("OTP sent successfully to " + email);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
    }
}
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String enteredOtp = request.get("otp");

        if (email == null || enteredOtp == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        String correctOtp = otpStore.get(email);

        if (correctOtp != null && correctOtp.equals(enteredOtp)) {
            return ResponseEntity.ok("OTP verified");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Email and new password (min 6 chars) required");
        }

        try (Connection con = AdminDBconnection.getConnection()) {
            // Check if email exists
            String checkQuery = "SELECT * FROM admindetails WHERE email_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found in database");
            }

            // Update password
            String updateQuery = "UPDATE admindetails SET password = ? WHERE email_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, email);
            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                otpStore.remove(email); // clear OTP after reset
                return ResponseEntity.ok("Password reset successful");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password reset failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while resetting password");
        }
    }

}
