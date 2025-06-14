package com.example.stage24.websocket.util;

import com.example.stage24.notification.model.ResponseNotification;
import com.example.stage24.websocket.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

/**
 * Utility class for WebSocket operations
 */
@Component
public class WebSocketUtil {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * Send a message to a specific topic
     */
    public void sendMessageToTopic(String topic, Message message) {
        messagingTemplate.convertAndSend(topic, message);
    }

    /**
     * Send a message to a specific user
     */
    public void sendMessageToUser(String userId, Message message) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", message);
    }

    /**
     * Convert a notification to a message
     */
    public Message convertNotificationToMessage(ResponseNotification notification) {
        Message message = new Message();
        message.setId(notification.getId());
        message.setType(Message.MessageType.NOTIFICATION);
        message.setFrom("SYSTEM");
        message.setText(notification.getMessage());
        message.setTopic("/topic/notifications/" + notification.getRecipientId());
        message.setTimestamp(notification.getTimestamp());
        message.setRead(notification.getRead());
        message.setRecipientId(notification.getRecipientId());
        return message;
    }
}