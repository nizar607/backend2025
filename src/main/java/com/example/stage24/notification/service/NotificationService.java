package com.example.stage24.notification.service;

import com.example.stage24.notification.domain.Notification;

import java.util.List;

/**
 * Service interface for handling notifications
 */
public interface NotificationService {

    /**
     * Get all notifications for the current user
     */
    List<Notification> getNotificationsForCurrentUser();

    /**
     * Mark a notification as read
     */
    void markAsRead(Long id);

    /**
     * Mark all notifications as read for the current user
     */
    void markAllAsRead();

    /**
     * Delete a notification
     */
    void deleteNotification(Long id);

    /**
     * Get notifications by type
     */
    List<Notification> getNotificationsByType(String type);

    /**
     * Get unread count for the current user
     */
    Long getUnreadCount();

    /**
     * Send a notification to a specific user
     */
    void sendNotification(Notification notification);

    /**
     * Send a notification to multiple users
     */
    void sendNotificationToUsers(Notification notification, List<String> userIds);

    /**
     * Send a broadcast notification to all users
     */
    void sendBroadcastNotification(Notification notification);
}