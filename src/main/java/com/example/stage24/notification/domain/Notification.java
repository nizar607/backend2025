package com.example.stage24.notification.domain;

import com.example.stage24.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity class for notifications that will be persisted in the database
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String type; // article, category, agent, message
    
    @Column(nullable = false, length = 500)
    private String message;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, name = "created_at")
    private Date timestamp;
    
    @Column(nullable = false, name = "is_read")
    private Boolean read = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_notification_recipient"))
    private User recipient;
    
    // Constructor without recipient for manual creation
    public Notification(String type, String message, Date timestamp, Boolean read) {
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read != null ? read : false;
    }
    
    // Constructor with recipient
    public Notification(String type, String message, User recipient) {
        this.type = type;
        this.message = message;
        this.timestamp = new Date();
        this.read = false;
        this.recipient = recipient;
    }
    
    /**
     * Get the recipient ID as a string
     */
    @Transient
    public String getRecipientId() {
        return recipient != null ? recipient.getId().toString() : null;
    }
    
    /**
     * Creates a copy of this notification for a different recipient
     * 
     * @param newRecipient The new recipient
     * @return A new notification instance with the same content for the new recipient
     */
    public Notification createCopyForRecipient(User newRecipient) {
        return new Notification(this.type, this.message, newRecipient);
    }
    
    /**
     * Mark this notification as read
     */
    public void markAsRead() {
        this.read = true;
    }
    
    /**
     * Mark this notification as unread
     */
    public void markAsUnread() {
        this.read = false;
    }
}