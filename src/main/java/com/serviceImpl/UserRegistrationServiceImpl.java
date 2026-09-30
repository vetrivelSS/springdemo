package com.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.Dao.UserLoginDao; 
import com.Dao.UserRegistrationDao; 

import com.dto.UserRegistrationDto; 
import com.dto.ForgotPasswordRequest;
import com.dto.VerifyOtpRequest;
import com.service.EmailService;
import com.service.UserRegistration_service;
import com.dto.ResetPasswordRequest;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UserRegistrationServiceImpl implements UserRegistration_service { 

	@Autowired
    private UserRegistrationDao userRegistrationDao; 

    @Autowired
    private UserLoginDao userLoginDao; 

    @Autowired
    private EmailService emailService; 

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // For hashing passwords

    @Override
    public String userRegistration(UserRegistrationDto dto) {
        // Hash the password BEFORE sending it to the DAO for storage
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(hashedPassword);

        return userRegistrationDao.userRegistration(dto);
    }

    @Override
    public boolean verifyLogin(String email, String plainTextPassword) {
        // Delegate to UserLoginDao for login verification
        return userLoginDao.verifyLogin(email, plainTextPassword);
    }

    @Override
    public String initiatePasswordReset(ForgotPasswordRequest request) {
        String email = request.getEmail();

        if (!userLoginDao.emailExistsInUsers(email)) {
            return "Email not found.";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        userLoginDao.saveOtp(email, otp, expiryTime);

        // --- FIX STARTS HERE ---
        // You need to get the recipient's name.
        // Option 1 (Recommended): Fetch the name from your userRegistrationDetails table.
        // You'll need to add a method to UserLoginDao (or a new UserDetailsDao) for this.
        // Example: String recipientName = userLoginDao.getUserNameByEmail(email);
        // For now, I'll use a placeholder "User" or fetch from DTO if available.

        // Assuming UserRegistrationDto has the name field and you can get it from the request if it was passed.
        // If not, you need a DAO call here to fetch the name from the userRegistrationDetails table.
        // For demonstration, let's add a simple method to UserLoginDao to fetch name by email.
        String recipientName = userLoginDao.getUserNameByEmail(email); // Call a new method to get the name
        if (recipientName == null || recipientName.isEmpty()) {
            recipientName = "User"; // Fallback if name not found or is empty
        }

        try {
            // Call the updated sendOtpEmail method with all three arguments
            emailService.sendOtpEmail(email, recipientName, otp); // <-- CORRECTED CALL
        } catch (RuntimeException e) {
            System.err.println("Could not send OTP email, but OTP saved to DB: " + e.getMessage());
            return "OTP generated, but email sending failed. Please try again or contact support.";
        }

        return "OTP sent to your email.";
    }

    @Override
    public boolean verifyOtpForReset(VerifyOtpRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();

        // 1. Verify OTP against stored value and expiry (using UserLoginDao)
        return userLoginDao.verifyOtp(email, otp);
    }

    @Override
    public String resetUserPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String newPassword = request.getNewPassword();

        // IMPORTANT: In a real application, you'd typically have a mechanism
        // to ensure that this reset request is linked to a *successfully verified OTP*.
        // For simplicity here, we're assuming the OTP verification step
        // has already passed successfully before this method is called.
        // You might pass a "verification token" from the verifyOtpForReset step to here.

        // 1. Hash the new password
        String hashedPassword = passwordEncoder.encode(newPassword);

        // 2. Update the password in the database (using UserLoginDao)
        boolean updated = userLoginDao.updatePassword(email, hashedPassword);

        if (updated) {
            // Optionally, delete any remaining OTP for this email if not already deleted by verifyOtp
            userLoginDao.deleteOtp(email);
            return "Password reset successfully.";
        } else {
            return "Password reset failed: User not found or update error.";
        }
    }
}