package com.example.stage24.notification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.example.stage24.notification.domain.Notification;

/**
 * Model class for notifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseNotification {
    private Long id;
    private String type; // article, category, agent, message
    private String message;
    private Date timestamp;
    private Boolean read;
    private String recipientId; // User ID of the recipient

    public ResponseNotification(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.timestamp = notification.getTimestamp();
        this.read = notification.getRead();
        this.recipientId = notification.getRecipientId(); 
    }
}