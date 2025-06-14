package com.example.stage24.notification.service.implementation;

import com.example.stage24.notification.domain.Notification;
import com.example.stage24.notification.model.ResponseNotification;
import com.example.stage24.notification.repository.NotificationRepository;
import com.example.stage24.notification.service.NotificationService;
import com.example.stage24.user.domain.User;
import com.example.stage24.user.repository.UserRepository;
import com.example.stage24.user.service.interfaces.IUserService;
import com.example.stage24.websocket.model.Message;
import com.example.stage24.websocket.util.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of the NotificationService interface
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    @Autowired
    private WebSocketUtil webSocketUtil;

    @Override
    public List<Notification> getNotificationsForCurrentUser() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to get notifications for non-existent user");
            return new ArrayList<>();
        }
        return notificationRepository.findByRecipientOrderByTimestampDesc(currentUser);
    }

    @Override
    public void markAsRead(Long id) {
        if (id == null) {
            logger.warn("Attempted to mark null notification ID as read");
            return;
        }
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to mark notification as read for non-existent user");
            return;
        }
        
        notificationRepository.findById(id).ifPresentOrElse(notification -> {
            if (notification.getRecipient().getId().equals(currentUser.getId())) {
                notification.setRead(true);
                notificationRepository.save(notification);
                logger.debug("Notification {} marked as read", id);
            } else {
                logger.warn("User {} attempted to mark notification {} belonging to another user as read", 
                    currentUser.getId(), id);
            }
        }, () -> logger.warn("Attempted to mark non-existent notification {} as read", id));
    }

    @Override
    public void markAllAsRead() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to mark all notifications as read for non-existent user");
            return;
        }
        
        List<Notification> notifications = notificationRepository.findByRecipientAndReadFalseOrderByTimestampDesc(currentUser);
        if (notifications.isEmpty()) {
            return;
        }
        
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

    }

    @Override
    public void deleteNotification(Long id) {
        if (id == null) {
            logger.warn("Attempted to delete null notification ID");
            return;
        }
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to delete notification for non-existent user");
            return;
        }
        
        notificationRepository.findById(id).ifPresentOrElse(notification -> {
            if (notification.getRecipient().getId().equals(currentUser.getId())) {
                notificationRepository.delete(notification);
                logger.debug("Notification {} deleted", id);
            } else {
                logger.warn("User {} attempted to delete notification {} belonging to another user", 
                    currentUser.getId(), id);
            }
        }, () -> logger.warn("Attempted to delete non-existent notification {}", id));
    }

    @Override
    public List<Notification> getNotificationsByType(String type) {
        if (type == null || type.isBlank()) {
            logger.warn("Attempted to get notifications with null or empty type");
            return new ArrayList<>();
        }
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to get notifications by type for non-existent user");
            return new ArrayList<>();
        }
        
        return notificationRepository.findByRecipientAndTypeOrderByTimestampDesc(currentUser, type);
    }

    @Override
    public Long getUnreadCount() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to get unread count for non-existent user");
            return Long.valueOf(0);
        }
        
        return  notificationRepository.countByRecipientAndReadFalse(currentUser);
    }

    @Override
    public void sendNotification(Notification notification) {
        if (notification == null) {
            logger.warn("Attempted to send null notification");
            return;
        }
        
       // Set recipient from recipientId if provided
        if (notification.getRecipient() == null && notification.getRecipientId() != null) {
            try {
                Long userId = Long.parseLong(notification.getRecipientId());
                Optional<User> recipientOpt = userRepository.findById(userId);
                
                if (recipientOpt.isPresent()) {
                    notification.setRecipient(recipientOpt.get());
                } else {
                    logger.warn("Recipient user with ID {} not found", userId);
                    return;
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid user ID format: {}", notification.getRecipientId(), e);
                return;
            }
        }
        
        // Validate notification
        if (notification.getRecipient() == null) {
            logger.warn("Cannot send notification without recipient");
            return;
        }
        
        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            logger.warn("Cannot send notification with empty message");
            return;
        }
        
        // Save to database
        try {
            notification = notificationRepository.save(notification);
            logger.debug("Notification saved to database: {}", notification.getId());
            ResponseNotification responseNotification = new ResponseNotification(notification);
            // Send via WebSocket
            Message message = webSocketUtil.convertNotificationToMessage(responseNotification);
            String recipientId = notification.getRecipient().getId().toString();
            
            messagingTemplate.convertAndSendToUser(recipientId, "/queue/notifications", message);
            messagingTemplate.convertAndSend("/topic/notifications/" + recipientId, message);
            logger.debug("Notification {} sent via WebSocket to user {}", notification.getId(), recipientId);
        } catch (Exception e) {
            logger.error("Error sending notification", e);
        }
    }

    @Override
    public void sendNotificationToUsers(Notification notification, List<String> userIds) {
        if (notification == null) {
            logger.warn("Attempted to send null notification to users");
            return;
        }
        
        if (CollectionUtils.isEmpty(userIds)) {
            logger.warn("Attempted to send notification to empty user list");
            return;
        }
        
        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            logger.warn("Cannot send notification with empty message");
            return;
        }
        
        for (String userId : userIds) {
            try {
                Long id = Long.parseLong(userId);
                userRepository.findById(id).ifPresentOrElse(user -> {
                    Notification userNotification = notification.createCopyForRecipient(user);
                    sendNotification(userNotification);
                }, () -> logger.warn("User with ID {} not found", id));
            } catch (NumberFormatException e) {
                logger.warn("Invalid user ID format: {}", userId);
            }
        }
    }

    @Override
    public void sendBroadcastNotification(Notification notification) {
        if (notification == null) {
            logger.warn("Attempted to broadcast null notification");
            return;
        }
        
        if (notification.getMessage() == null || notification.getMessage().isBlank()) {
            logger.warn("Cannot broadcast notification with empty message");
            return;
        }
        
        logger.info("Sending broadcast notification: {}", notification.getMessage());
        
        try {
            // Get all users
            List<User> users = userService.getUsers();
            
            if (users == null || users.isEmpty()) {
                logger.warn("No users found for broadcast notification");
                return;
            }
            
            // Send to general topic first (for immediate display)
            Message broadcastMessage = new Message();
            broadcastMessage.setType(Message.MessageType.NOTIFICATION);
            broadcastMessage.setFrom("SYSTEM");
            broadcastMessage.setText(notification.getMessage());
            broadcastMessage.setTimestamp(new Date());
            
            messagingTemplate.convertAndSend("/topic/notifications", broadcastMessage);
            logger.debug("Broadcast notification sent to general topic");
            
            // Process individual user notifications asynchronously
            CompletableFuture.runAsync(() -> {
                try {
                    for (User user : users) {
                        if (user != null && user.getId() != null) {
                            Notification userNotification = notification.createCopyForRecipient(user);
                            
                            // Save to database
                            Notification savedNotification = notificationRepository.save(userNotification);
                            
                            // Send individual notification
                            try {
                                ResponseNotification responseNotification = new ResponseNotification(savedNotification);
                                Message userMessage = webSocketUtil.convertNotificationToMessage(responseNotification);
                                String recipientId = user.getId().toString();
                                messagingTemplate.convertAndSendToUser(recipientId, "/queue/notifications", userMessage);
                            } catch (Exception e) {
                                logger.warn("Failed to send notification to user {}: {}", user.getId(), e.getMessage());
                            }
                        }
                    }
                    logger.debug("Completed processing individual notifications for {} users", users.size());
                } catch (Exception e) {
                    logger.error("Error processing individual notifications", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error sending broadcast notification", e);
        }
    }
    
    private User getCurrentUser() {
        Optional<User> currentUser = userService.getConnectedUser();
        if (currentUser.isEmpty()) {
            logger.debug("No user connected in current context");
        }
        return currentUser.orElse(null);
    }
}