# Email Service Setup Guide

## Overview
This application includes an email service that integrates with Proton Mail via Proton Bridge to send emails.

## Configuration

### 1. Proton Bridge Setup
Before using the email service, you need to:

1. **Install and Configure Proton Bridge**
   - Download Proton Bridge from the official Proton website
   - Install and run Proton Bridge on your system
   - Log in with your Proton Mail credentials
   - Ensure Bridge is running and connected

2. **Verify Bridge Connection**
   - Proton Bridge should be running on localhost (127.0.0.1)
   - Default SMTP port is 1025
   - Bridge provides local SMTP server for secure email sending

### 2. SMTP Credentials
The application uses the following Proton Mail SMTP configuration:
- **Host**: 127.0.0.1 (Proton Bridge local server)
- **Port**: 1025 (Proton Bridge SMTP port)
- **Username**: nizarouertani9@protonmail.com
- **Password**: 855oIHsECAOXWmNW1zb-fA (Bridge-generated password)
- **Security**: STARTTLS (with relaxed SSL verification for local connection)

### 3. Testing the Email Service

#### Test Endpoints Available:

1. **Send Test Email**
   ```bash
   POST /api/email/test
   Content-Type: application/json
   
   {
     "to": "recipient@example.com",
     "subject": "Test Email",
     "content": "This is a test email"
   }
   ```

2. **Send Welcome Email**
   ```bash
   POST /api/email/welcome
   Content-Type: application/json
   
   {
     "to": "recipient@example.com",
     "firstName": "John",
     "lastName": "Doe"
   }
   ```

3. **Send HTML Email**
   ```bash
   POST /api/email/html
   Content-Type: application/json
   
   {
     "to": "recipient@example.com",
     "subject": "HTML Test",
     "htmlContent": "<h1>Hello</h1><p>This is HTML content</p>"
   }
   ```

### 4. Integration with User Registration

The email service is automatically integrated with user registration:
- When a user successfully registers, a welcome email is sent
- The email sending is non-blocking (registration won't fail if email fails)
- Logs are generated for successful and failed email attempts

### 5. Troubleshooting

#### Common Issues:

1. **Socket Timeout / Connection Failed**
   - **Ensure Proton Bridge is running**: Check that Proton Bridge application is started and connected
   - **Verify Bridge status**: Look for Bridge icon in system tray (should show connected status)
   - **Check Bridge logs**: Open Bridge settings to view connection logs
   - **Restart Bridge**: Close and restart Proton Bridge if connection issues persist

2. **Authentication Failed**
   - **Verify Bridge password**: Use the Bridge-generated password, not your Proton Mail password
   - **Check username format**: Ensure full email address is used as username
   - **Bridge account sync**: Ensure your Proton Mail account is properly synced in Bridge

3. **Email not received**
   - **Check Proton Mail sent folder**: Verify emails are being sent from your Proton account
   - **Recipient spam/junk folders**: Check if emails are filtered as spam
   - **Bridge connection**: Ensure Bridge maintains stable connection during sending

4. **SSL/TLS Issues**
   - **Local certificate trust**: Bridge uses local certificates that may need trust configuration
   - **Firewall interference**: Check if firewall is blocking local Bridge communication
   - **Port availability**: Ensure port 1025 is available and not blocked

### 6. Production Considerations

1. **Move out of SES Sandbox**
   - Request production access from AWS
   - This allows sending to any email address

2. **Email Templates**
   - Consider using SES templates for better management
   - Implement template-based email service

3. **Error Handling**
   - Implement retry mechanisms
   - Add email queue for high-volume sending
   - Monitor email delivery rates

4. **Security**
   - Store SMTP credentials in environment variables
   - Use AWS IAM roles instead of access keys when possible
   - Implement rate limiting for email endpoints