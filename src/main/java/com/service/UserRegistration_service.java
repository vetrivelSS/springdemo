package com.service;

import org.springframework.stereotype.Service;

import com.dto.ForgotPasswordRequest;
import com.dto.ResetPasswordRequest;
import com.dto.UserRegistrationDto;
import com.dto.VerifyOtpRequest;
@Service
public interface UserRegistration_service {

	  String userRegistration(UserRegistrationDto dto);
	    boolean verifyLogin(String email, String plainTextPassword);

	    // Methods for Forgot Password flow
	    String initiatePasswordReset(ForgotPasswordRequest request);
	    boolean verifyOtpForReset(VerifyOtpRequest request);
	    String resetUserPassword(ResetPasswordRequest request);
}
