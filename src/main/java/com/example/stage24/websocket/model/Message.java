package com.example.stage24.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * A simple message model for WebSocket communication
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private String from;
    private String text;
    private String topic;
    private MessageType type;
    private Date timestamp;
    private boolean read;
    private String recipientId;
    
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        NOTIFICATION
    }
}