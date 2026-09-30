package com.controller; // Adjust package as per your project structure

import java.util.Arrays; // Keep if used elsewhere, otherwise can remove

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Corrected DTO imports (assuming they are in com.dto)
import com.dto.ScheduleAdminDto;
import com.dto.ServiceResponseDto;
import com.dto.UserRegistrationDto;
import com.dto.ForgotPasswordRequest;
import com.dto.VerifyOtpRequest;
import com.dto.ResetPasswordRequest;

// Corrected Service interface import (assuming it's in com.service)
import com.service.UserRegistration_service;

@RequestMapping("/UserRegistration_controller") // Consider renaming this to just "/users" for REST best practices
@RestController
@CrossOrigin(origins = "*")
public class UserRegistration_controller {

	@Autowired
	private UserRegistration_service sr; // Autowiring your service interface

    /**
     * API endpoint for user registration.
     * Receives user details and attempts to register them.
     *
     * @param dto UserRegistrationDto containing user details.
     * @return ResponseEntity with success/failure message and appropriate HTTP status.
     */
    @PostMapping("/register") // Maps POST requests to /UserRegistration_controller/register
    public ResponseEntity<String> userRegistration(@RequestBody UserRegistrationDto dto) {
        System.out.println("Registration request received for: " + dto.getEmail());
        try {
            String status = sr.userRegistration(dto); // Delegate registration to the service layer
            if ("success".equals(status)) {
                return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED); // 201 Created for success
            } else {
                return new ResponseEntity<>("Registration failed: " + status, HttpStatus.BAD_REQUEST); // 400 Bad Request for specific failures
            }
        } catch (RuntimeException e) {
            System.err.println("Error during registration: " + e.getMessage());
            // Catches RuntimeException (e.g., duplicate entry from DAO) and returns 500 Internal Server Error
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API endpoint for user login.
     * Receives user email and plain-text password and attempts to authenticate them.
     *
     * @param loginDto UserRegistrationDto containing email and password for login.
     * @return ResponseEntity with success/failure message and appropriate HTTP status.
     */
    @PostMapping("/login") // Maps POST requests to /UserRegistration_controller/login
    public ResponseEntity<String> userLogin(@RequestBody UserRegistrationDto loginDto) {
        System.out.println("Login request received for email: " + loginDto.getEmail());
        try {
            boolean loginSuccess = sr.verifyLogin(loginDto.getEmail(), loginDto.getPassword()); // Delegate login verification to service

            if (loginSuccess) {
                return new ResponseEntity<>("Login successful!", HttpStatus.OK); // 200 OK for successful login
            } else {
                // For security, return a generic message for invalid credentials (don't distinguish between email not found vs. wrong password)
                return new ResponseEntity<>("Invalid email or password.", HttpStatus.UNAUTHORIZED); // 401 Unauthorized for failed login
            }
        } catch (RuntimeException e) {
            System.err.println("Error during login: " + e.getMessage());
            return new ResponseEntity<>("Login failed due to an internal error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error for unexpected errors
        }
    }

    /**
     * API endpoint to initiate the password reset process.
     * Receives user's email, generates an OTP, saves it, and sends it via email.
     *
     * @param request ForgotPasswordRequest containing the user's email.
     * @return ResponseEntity indicating if OTP was sent or if email was not found.
     */
    @PostMapping("/forgot-password/initiate") // Maps POST requests to /UserRegistration_controller/forgot-password/initiate
    public ResponseEntity<String> initiatePasswordReset(@RequestBody ForgotPasswordRequest request) {
        System.out.println("Forgot password initiate request for email: " + request.getEmail());
        try {
            String result = sr.initiatePasswordReset(request); // Delegate to service
            if ("Email not found.".equals(result)) {
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND); // 404 Not Found if email doesn't exist
            }
            // If email sending failed, the service returns a specific message
            if (result.contains("email sending failed")) {
                return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR); // 500 if email send failed
            }
            return new ResponseEntity<>(result, HttpStatus.OK); // 200 OK if OTP initiated and email sent
        } catch (RuntimeException e) {
            System.err.println("Error initiating password reset: " + e.getMessage());
            return new ResponseEntity<>("Failed to initiate password reset: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 for other errors
        }
    }

    /**
     * API endpoint to verify the OTP sent for password reset.
     *
     * @param request VerifyOtpRequest containing email and the OTP entered by the user.
     * @return ResponseEntity indicating if OTP is valid/expired/incorrect.
     */
    @PostMapping("/forgot-password/verify-otp") // Maps POST requests to /UserRegistration_controller/forgot-password/verify-otp
    public ResponseEntity<String> verifyOtpForReset(@RequestBody VerifyOtpRequest request) {
        System.out.println("Verify OTP request for email: " + request.getEmail() + ", OTP: " + request.getOtp());
        try {
            boolean verified = sr.verifyOtpForReset(request); // Delegate OTP verification to service
            if (verified) {
                return new ResponseEntity<>("OTP verified successfully. You can now reset your password.", HttpStatus.OK); // 200 OK for successful verification
            } else {
                return new ResponseEntity<>("Invalid or expired OTP.", HttpStatus.BAD_REQUEST); // 400 Bad Request for invalid/expired OTP
            }
        } catch (RuntimeException e) {
            System.err.println("Error verifying OTP: " + e.getMessage());
            return new ResponseEntity<>("Failed to verify OTP: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 for other errors
        }
    }

    /**
     * API endpoint to reset the user's password after OTP verification.
     *
     * @param request ResetPasswordRequest containing email and the new plain-text password.
     * @return ResponseEntity indicating success or failure of password reset.
     */
    @PostMapping("/forgot-password/reset") // Maps POST requests to /UserRegistration_controller/forgot-password/reset
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        System.out.println("Password reset request for email: " + request.getEmail());
        // In a real app, you might add a check here to ensure OTP was recently verified
        // e.g., by passing a token from the verify-otp step to this step.
        try {
            String result = sr.resetUserPassword(request); // Delegate password reset to service
            if ("Password reset successfully.".equals(result)) {
                return new ResponseEntity<>(result, HttpStatus.OK); // 200 OK for successful password reset
            } else {
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST); // 400 Bad Request for other reset failures
            }
        } catch (RuntimeException e) {
            System.err.println("Error resetting password: " + e.getMessage());
            return new ResponseEntity<>("Failed to reset password: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 for other errors
        }
    }
}