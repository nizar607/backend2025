package com.example.stage24.notification.repository;

import com.example.stage24.notification.domain.Notification;
import com.example.stage24.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for Notification entity
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific recipient
     */
    List<Notification> findByRecipientOrderByTimestampDesc(User recipient);
    
    /**
     * Find all unread notifications for a specific recipient
     */
    List<Notification> findByRecipientAndReadFalseOrderByTimestampDesc(User recipient);
    
    /**
     * Find all notifications for a specific recipient and type
     */
    List<Notification> findByRecipientAndTypeOrderByTimestampDesc(User recipient, String type);
    
    /**
     * Find all unread notifications for a specific recipient and type
     */
    List<Notification> findByRecipientAndTypeAndReadFalseOrderByTimestampDesc(User recipient, String type);
    
    /**
     * Count unread notifications for a specific recipient
     */
    Long countByRecipientAndReadFalse(User recipient);
    
    /**
     * Count unread notifications for a specific recipient and type
     */
    Long countByRecipientAndTypeAndReadFalse(User recipient, String type);
    
    /**
     * Mark all notifications as read for a specific recipient
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient = :recipient AND n.read = false")
    int markAllAsReadForRecipient(@Param("recipient") User recipient);
    
    /**
     * Delete all notifications older than a specified timestamp
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.timestamp < :cutoffDate")
    int deleteNotificationsOlderThan(@Param("cutoffDate") Date cutoffDate);
    
    /**
     * Delete read notifications older than a specified timestamp
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.read = true AND n.timestamp < :cutoffDate")
    int deleteReadNotificationsOlderThan(@Param("cutoffDate") Date cutoffDate);
    
    /**
     * Delete all notifications for a specific recipient
     */
    @Modifying
    @Transactional
    void deleteByRecipient(User recipient);
}