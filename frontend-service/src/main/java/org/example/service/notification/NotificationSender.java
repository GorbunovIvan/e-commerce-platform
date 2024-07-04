package org.example.service.notification;

import org.springframework.stereotype.Service;

@Service
public interface NotificationSender {
    void sendNotification(String message);
}
