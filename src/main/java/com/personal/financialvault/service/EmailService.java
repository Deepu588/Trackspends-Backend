package com.personal.financialvault.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${app.backend.url}")
    private String backendUrl;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String token) {
      //  String resetLink = "http://localhost:3000/reset-password?token=" + token;
        String resetLink = frontendUrl
                + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("TRACKSPENDS — Password Reset Request");
        message.setText(
                "Hello,\n\n" +
                        "You requested to reset your password.\n\n" +
                        "Click the link below to reset it:\n" +
                        resetLink + "\n\n" +
                        "This link is valid for 15 minutes only.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "— Trackspends Team"
        );

        mailSender.send(message);
    }

    public void sendVerificationEmail(String toEmail, String token) {
        //String verificationLink = "http://localhost:3000/verify-email?token=" + token;
        String verificationLink = frontendUrl
                + "/api/auth/verify-email?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Financial Vault — Verify Your Email");
        message.setText(
                "Hello,\n\n" +
                        "Thank you for registering with Financial Vault!\n\n" +
                        "Please click the link below to verify your email:\n" +
                        verificationLink + "\n\n" +
                        "This link is valid for 24 hours.\n\n" +
                        "If you did not register, please ignore this email.\n\n" +
                        "— Financial Vault Team"
        );
        mailSender.send(message);
    }

    public void sendReportEmail(String toEmail, String reportContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Financial Vault — Your Expense Report");
        message.setText(reportContent);
        mailSender.send(message);
    }


}