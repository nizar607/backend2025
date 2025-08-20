package com.example.stage24.email.controller;

import com.example.stage24.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test")
    public ResponseEntity<?> sendTestEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String subject = request.getOrDefault("subject", "Test Email from Platform");
            String content = request.getOrDefault("content", "This is a test email to verify the email service is working correctly.");

            if (to == null || to.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email address is required"));
            }

            emailService.sendTestEmail(to, subject, content);
            log.info("Test email sent successfully to: {}", to);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test email sent successfully",
                "to", to,
                "subject", subject
            ));
        } catch (Exception e) {
            log.error("Failed to send test email", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to send test email: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/welcome")
    public ResponseEntity<?> sendWelcomeEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String firstName = request.getOrDefault("firstName", "User");
            String lastName = request.getOrDefault("lastName", "");

            if (to == null || to.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email address is required"));
            }

            emailService.sendWelcomeEmail(to, firstName, lastName);
            log.info("Welcome email sent successfully to: {}", to);
            
            return ResponseEntity.ok(Map.of(
                "message", "Welcome email sent successfully",
                "to", to,
                "firstName", firstName,
                "lastName", lastName
            ));
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to send welcome email: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/html")
    public ResponseEntity<?> sendHtmlEmail(@RequestBody Map<String, String> request) {
        try {
            String to = request.get("to");
            String subject = request.getOrDefault("subject", "HTML Test Email");
            String htmlContent = request.getOrDefault("htmlContent", 
                "<h1>Test HTML Email</h1><p>This is a <strong>test HTML email</strong> with formatting.</p>");

            if (to == null || to.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email address is required"));
            }

            emailService.sendHtmlEmail(to, subject, htmlContent);
            log.info("HTML email sent successfully to: {}", to);
            
            return ResponseEntity.ok(Map.of(
                "message", "HTML email sent successfully",
                "to", to,
                "subject", subject
            ));
        } catch (Exception e) {
            log.error("Failed to send HTML email", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to send HTML email: " + e.getMessage()
            ));
        }
    }
}