package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendOtpEmail(String toEmail, String recipientName, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // 'true' for multipart message (for attachments/inline)

            // IMPORTANT: This 'from' address should match the 'spring.mail.username' in application.properties
            // And it should be a valid email address that your SMTP server is authorized to send from.
            helper.setFrom("svetrivel002@gmail.com"); // Your configured sender email
            helper.setTo(toEmail);
            helper.setSubject("Health Walk - Your Password Reset OTP");

            // HTML email content
            String htmlMsg = "<html><body>" +
                "<div style='font-family:Arial,sans-serif; max-width:600px; margin:auto; padding:20px; border:1px solid #ddd; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>" +
                "<h1 style='color:#007bff; text-align:center;'>Health Walk</h1>" + // Added a title/logo space
                "<hr style='border: 0; height: 1px; background: #eee; margin: 20px 0;'>" +
                "<h2 style='color:#333;'>Dear " + recipientName + ",</h2>" +
                "<p style='font-size:16px; line-height:1.5;'>Your One-Time Password (OTP) for verification is:</p>" +
                "<div style='text-align:center; margin:25px 0;'>" +
                "<h1 style='color:#007bff; font-size:36px; margin:0; padding:15px 25px; background-color:#f0f8ff; border-radius:10px; display:inline-block; border:1px solid #a8d5ff;'>🔐 OTP: " + otp + "</h1>" +
                "</div>" +
                "<p style='font-size:14px; color:#555;'>This OTP is valid for <strong>5 minutes</strong>. Please do not share this with anyone.</p>" +
                "<p style='font-size:14px; color:#555;'>If you did not request a password reset, please ignore this email.</p>" +
                "<img src='cid:bannerImage' alt='Health Walk Banner' style='width:100%; max-width:560px; height:auto; display:block; margin:20px auto; border-radius: 8px;'/>" + // Adjusted image styles
                "<p style='font-size:14px; color:#333;'>Best regards,<br><strong>Health Walk Team</strong></p>" +
                "<hr style='border: 0; height: 1px; background: #eee; margin: 20px 0;'>" +
                "<p style='font-size:12px; color:#888; text-align:center;'>This is an automated email, please do not reply.</p>" +
                "</div>" +
                "</body></html>";

            helper.setText(htmlMsg, true); // 'true' indicates HTML content

            // Attach banner image from resources (classpath)
            // The 'cid:bannerImage' in the HTML links to this attachment
            helper.addInline("bannerImage", new ClassPathResource("static/images/logo.png"));

            mailSender.send(message);
            System.out.println("HTML OTP email sent successfully to: " + toEmail);
        } catch (MailException e) {
            System.err.println("Error sending HTML OTP email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send HTML OTP email.", e);
        } catch (jakarta.mail.MessagingException e) { // Catch MessagingException for MimeMessageHelper issues
            System.err.println("MessagingException during HTML OTP email send to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send HTML OTP email due to messaging error.", e);
        }
    }

}
