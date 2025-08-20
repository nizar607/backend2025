package com.example.stage24.email.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String firstName, String lastName);
    void sendTestEmail(String to, String subject, String content);
    void sendHtmlEmail(String to, String subject, String htmlContent);
}