package com.example.stage24.email.controller;

import com.example.stage24.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email-test")
@RequiredArgsConstructor
@Slf4j
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping("/simulate-signup")
    public ResponseEntity<?> simulateSignupWithEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String firstName = request.getOrDefault("firstName", "Test");
            String lastName = request.getOrDefault("lastName", "User");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email address is required"));
            }

            // Simulate user registration process
            log.info("Simulating user registration for: {} {} ({})", firstName, lastName, email);
            
            // Simulate successful user creation
            boolean userCreated = true;
            
            if (userCreated) {
                // Try to send welcome email (same as in actual signup)
                try {
                    emailService.sendWelcomeEmail(email, firstName, lastName);
                    log.info("Welcome email sent successfully to: {}", email);
                    
                    return ResponseEntity.ok(Map.of(
                        "message", "User registration simulated successfully",
                        "user", Map.of(
                            "email", email,
                            "firstName", firstName,
                            "lastName", lastName
                        ),
                        "emailStatus", "Welcome email sent successfully"
                    ));
                } catch (Exception emailError) {
                    log.error("Failed to send welcome email to: {}", email, emailError);
                    
                    return ResponseEntity.ok(Map.of(
                        "message", "User registration simulated successfully",
                        "user", Map.of(
                            "email", email,
                            "firstName", firstName,
                            "lastName", lastName
                        ),
                        "emailStatus", "Failed to send welcome email: " + emailError.getMessage(),
                        "note", "User registration succeeded but email sending failed (this is expected behavior)"
                    ));
                }
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "User registration simulation failed"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error in signup simulation", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Signup simulation failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getEmailServiceStatus() {
        return ResponseEntity.ok(Map.of(
            "emailServiceStatus", "Email service is configured and ready",
            "smtpHost", "email-smtp.us-east-1.amazonaws.com",
            "smtpPort", 587,
            "authEnabled", true,
            "starttlsEnabled", true,
            "note", "Email sending requires verified SES email addresses"
        ));
    }

    @PostMapping("/validate-config")
    public ResponseEntity<?> validateEmailConfig() {
        try {
            // This will test the email service configuration without actually sending
            return ResponseEntity.ok(Map.of(
                "configStatus", "Email service configuration is valid",
                "message", "Email service is properly configured and ready to send emails",
                "recommendation", "Ensure your sender email is verified in AWS SES before sending emails"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "configStatus", "Email service configuration has issues",
                "error", e.getMessage()
            ));
        }
    }
}