package com.example.stage24.email.service.impl;

import com.example.stage24.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${email.from}")
    private String fromEmail;

    @Override
    public void sendWelcomeEmail(String to, String firstName, String lastName) {
        try {
            String subject = "Welcome to Our Platform!";
            String htmlContent = buildWelcomeEmailTemplate(firstName, lastName);
            sendHtmlEmail(to, subject, htmlContent);
            log.info("Welcome email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", to, e);
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    @Override
    public void sendTestEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Test email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send test email to: {}", to, e);
            throw new RuntimeException("Failed to send test email", e);
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    private String buildWelcomeEmailTemplate(String firstName, String lastName) {
        return "<!DOCTYPE html>\n" +
                "<html lang='en'>\n" +
                "<head>\n" +
                "    <meta charset='UTF-8'>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <title>Welcome to Stage24 - Your Journey Begins!</title>\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #2c3e50; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }\n" +
                "        .email-container { max-width: 650px; margin: 40px auto; border-radius: 20px; overflow: hidden; box-shadow: 0 20px 40px rgba(0,0,0,0.1); background: white; }\n" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 60px 40px; text-align: center; position: relative; }\n" +
                "        .header::before { content: ''; position: absolute; top: 0; left: 0; right: 0; bottom: 0; background: url('data:image/svg+xml,<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\"><defs><pattern id=\"grain\" width=\"100\" height=\"100\" patternUnits=\"userSpaceOnUse\"><circle cx=\"50\" cy=\"50\" r=\"1\" fill=\"white\" opacity=\"0.1\"/></pattern></defs><rect width=\"100\" height=\"100\" fill=\"url(%23grain)\"/></svg>'); opacity: 0.3; }\n" +
                "        .header h1 { color: white; font-size: 2.5em; font-weight: 300; margin-bottom: 10px; position: relative; z-index: 1; }\n" +
                "        .header p { color: rgba(255,255,255,0.9); font-size: 1.2em; position: relative; z-index: 1; }\n" +
                "        .welcome-badge { background: rgba(255,255,255,0.2); backdrop-filter: blur(10px); padding: 10px 25px; border-radius: 50px; display: inline-block; margin-top: 20px; border: 1px solid rgba(255,255,255,0.3); }\n" +
                "        .content { padding: 50px 40px; background: white; }\n" +
                "        .greeting { font-size: 1.8em; color: #2c3e50; margin-bottom: 25px; font-weight: 600; }\n" +
                "        .message { font-size: 1.1em; color: #5a6c7d; margin-bottom: 30px; line-height: 1.8; }\n" +
                "        .features { margin: 40px 0; }\n" +
                "        .feature-item { display: flex; align-items: center; margin: 20px 0; padding: 20px; background: linear-gradient(135deg, #f8f9ff 0%, #e8f2ff 100%); border-radius: 15px; border-left: 4px solid #667eea; }\n" +
                "        .feature-icon { width: 50px; height: 50px; background: linear-gradient(135deg, #667eea, #764ba2); border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 20px; font-size: 1.5em; }\n" +
                "        .feature-text h3 { color: #2c3e50; margin-bottom: 5px; font-weight: 600; }\n" +
                "        .feature-text p { color: #5a6c7d; font-size: 0.95em; }\n" +
                "        .cta-section { text-align: center; margin: 50px 0; }\n" +
                "        .cta-button { display: inline-block; padding: 18px 40px; background: linear-gradient(135deg, #667eea, #764ba2); color: white; text-decoration: none; border-radius: 50px; font-weight: 600; font-size: 1.1em; transition: all 0.3s ease; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); }\n" +
                "        .cta-button:hover { transform: translateY(-2px); box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6); }\n" +
                "        .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 20px; margin: 40px 0; }\n" +
                "        .stat-box { text-align: center; padding: 25px; background: linear-gradient(135deg, #f8f9ff, #e8f2ff); border-radius: 15px; }\n" +
                "        .stat-number { font-size: 2em; font-weight: 700; color: #667eea; }\n" +
                "        .stat-label { color: #5a6c7d; font-size: 0.9em; margin-top: 5px; }\n" +
                "        .footer { background: #2c3e50; color: white; padding: 40px; text-align: center; }\n" +
                "        .footer-content h3 { margin-bottom: 15px; color: #ecf0f1; }\n" +
                "        .footer-content p { color: #bdc3c7; margin-bottom: 20px; }\n" +
                "        .social-links { margin: 20px 0; }\n" +
                "        .social-link { display: inline-block; margin: 0 10px; color: #ecf0f1; font-size: 1.2em; text-decoration: none; }\n" +
                "        .divider { height: 1px; background: linear-gradient(90deg, transparent, #667eea, transparent); margin: 30px 0; }\n" +
                "        @media (max-width: 600px) { .email-container { margin: 20px; } .header, .content { padding: 30px 20px; } .stats { grid-template-columns: 1fr; } }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='email-container'>\n" +
                "        <div class='header'>\n" +
                "            <h1>Welcome to Stage24</h1>\n" +
                "            <p>Your Digital Experience Platform</p>\n" +
                "            <div class='welcome-badge'>🎉 Account Created Successfully</div>\n" +
                "        </div>\n" +
                "        <div class='content'>\n" +
                "            <div class='greeting'>Hello " + firstName + " " + lastName + "! 🌟</div>\n" +
                "            <div class='message'>\n" +
                "                Thank you for joining Stage24! We're absolutely thrilled to have you as part of our growing community.\n" +
                "                Your account has been successfully created and you're now ready to unlock amazing digital experiences.\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class='stats'>\n" +
                "                <div class='stat-box'>\n" +
                "                    <div class='stat-number'>24/7</div>\n" +
                "                    <div class='stat-label'>Support Available</div>\n" +
                "                </div>\n" +
                "                <div class='stat-box'>\n" +
                "                    <div class='stat-number'>1000+</div>\n" +
                "                    <div class='stat-label'>Happy Users</div>\n" +
                "                </div>\n" +
                "                <div class='stat-box'>\n" +
                "                    <div class='stat-number'>50+</div>\n" +
                "                    <div class='stat-label'>Features</div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class='features'>\n" +
                "                <div class='feature-item'>\n" +
                "                    <div class='feature-icon'>🚀</div>\n" +
                "                    <div class='feature-text'>\n" +
                "                        <h3>Lightning Fast</h3>\n" +
                "                        <p>Experience blazing-fast performance with our optimized platform</p>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class='feature-item'>\n" +
                "                    <div class='feature-icon'>🔒</div>\n" +
                "                    <div class='feature-text'>\n" +
                "                        <h3>Secure & Private</h3>\n" +
                "                        <p>Your data is protected with enterprise-grade security</p>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class='feature-item'>\n" +
                "                    <div class='feature-icon'>🎨</div>\n" +
                "                    <div class='feature-text'>\n" +
                "                        <h3>Beautiful Design</h3>\n" +
                "                        <p>Intuitive interface designed for the best user experience</p>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class='divider'></div>\n" +
                "\n" +
                "            <div class='cta-section'>\n" +
                "                <a href='#' class='cta-button'>Start Your Journey</a>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <div class='footer'>\n" +
                "            <div class='footer-content'>\n" +
                "                <h3>Need Help Getting Started?</h3>\n" +
                "                <p>Our support team is here to help you make the most of Stage24</p>\n" +
                "                <div class='social-links'>\n" +
                "                    <a href='mailto:support@stage24.com' class='social-link'>📧 Email Support</a>\n" +
                "                    <a href='tel:+1234567890' class='social-link'>📞 Call Us</a>\n" +
                "                    <a href='#' class='social-link'>💬 Live Chat</a>\n" +
                "                </div>\n" +
                "                <p style='margin-top: 30px; font-size: 0.9em; color: #95a5a6;'>\n" +
                "                    &copy; 2024 Stage24. All rights reserved.<br>\n" +
                "                    Made with ❤️ for amazing digital experiences\n" +
                "                </p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}