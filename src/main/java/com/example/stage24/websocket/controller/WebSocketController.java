package com.example.stage24.websocket.controller;

import com.example.stage24.notification.domain.Notification;
import com.example.stage24.notification.service.NotificationService;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.service.interfaces.IUserService;
import com.example.stage24.websocket.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling WebSocket messages
 */
@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IUserService userService;

    /**
     * Handles messages sent to /app/chat.sendMessage and broadcasts to
     * /topic/public
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message, Principal principal) {
        if (message != null && principal != null) {
            // Set the authenticated username as the sender
            message.setFrom(principal.getName());
            logger.debug("User {} sent a message to public chat", principal.getName());
        }
        return message;
    }

    /**
     * Note: Category and article notifications are now handled automatically
     * by their respective service implementations when items are created.
     * This eliminates the need for client-initiated notification messages.
     */

    /**
     * Handles user join events and adds the user to the WebSocket session
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        if (headerAccessor != null && principal != null) {
            // Add username in web socket session using the authenticated principal
            String username = principal.getName();
            headerAccessor.getSessionAttributes().put("username", username);
            message.setFrom(username);
            logger.debug("User {} joined the chat", username);
        }
        return message;
    }

    /**
     * Handles notification messages sent to specific users
     */
    @MessageMapping("/notification.send")
    @SendToUser("/queue/reply")
    @PreAuthorize("hasRole('USER')")
    public Message sendNotification(@Payload Notification notification, Principal principal) {
        try {
            // Validate the notification
            if (notification == null || notification.getMessage() == null || notification.getMessage().isBlank()) {
                return createErrorMessage("Invalid notification content");
            }

            // Set sender information
            Optional<User> currentUser = userService.getConnectedUser();
            if (currentUser.isPresent()) {
                // Only allow sending if the user has appropriate permissions
                notificationService.sendNotification(notification);
                logger.debug("User {} sent a notification to recipient {}",
                        principal.getName(), notification.getRecipientId());
                return createSuccessMessage("Notification sent successfully");
            } else {
                return createErrorMessage("Authentication required");
            }
        } catch (Exception e) {
            logger.error("Error sending notification", e);
            return createErrorMessage("Error sending notification");
        }
    }

    /**
     * Handles broadcast notification messages sent to all users
     */
    @MessageMapping("/notification.broadcast")
    @SendToUser("/queue/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public Message broadcastNotification(@Payload Notification notification, Principal principal) {
        try {
            // Validate the notification
            if (notification == null || notification.getMessage() == null || notification.getMessage().isBlank()) {
                return createErrorMessage("Invalid notification content");
            }

            notificationService.sendBroadcastNotification(notification);
            logger.info("User {} sent a broadcast notification: {}",
                    principal.getName(), notification.getMessage());
            return createSuccessMessage("Broadcast notification sent successfully");
        } catch (Exception e) {
            logger.error("Error sending broadcast notification", e);
            return createErrorMessage("Error sending broadcast notification");
        }
    }

    /**
     * Handles sending notification to multiple users
     */
    @MessageMapping("/notification.sendToMany")
    @SendToUser("/queue/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public Message sendToManyUsers(@Payload NotificationRequest request, Principal principal) {
        try {
            // Validate the request
            if (request == null || request.getNotification() == null ||
                    request.getNotification().getMessage() == null ||
                    request.getNotification().getMessage().isBlank() ||
                    request.getUserIds() == null || request.getUserIds().isEmpty()) {
                return createErrorMessage("Invalid notification request");
            }

            notificationService.sendNotificationToUsers(request.getNotification(), request.getUserIds());
            logger.info("User {} sent a notification to {} users",
                    principal.getName(), request.getUserIds().size());
            return createSuccessMessage("Notification sent to " + request.getUserIds().size() + " users");
        } catch (Exception e) {
            logger.error("Error sending notification to multiple users", e);
            return createErrorMessage("Error sending notification to multiple users");
        }
    }

    /**
     * Creates a success message response
     */
    private Message createSuccessMessage(String text) {
        Message message = new Message();
        message.setType(Message.MessageType.NOTIFICATION);
        message.setFrom("SYSTEM");
        message.setText(text);
        return message;
    }

    /**
     * Creates an error message response
     */
    private Message createErrorMessage(String text) {
        Message message = new Message();
        message.setType(Message.MessageType.NOTIFICATION);
        message.setFrom("SYSTEM");
        message.setText(text);
        return message;
    }

    /**
     * Request wrapper for sending notifications to multiple users
     */
    public static class NotificationRequest {
        private Notification notification;
        private List<String> userIds;

        public Notification getNotification() {
            return notification;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        public List<String> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<String> userIds) {
            this.userIds = userIds;
        }
    }

}