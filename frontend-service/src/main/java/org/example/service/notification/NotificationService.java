package org.example.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSender notificationSender;

    public void sendNotification(String content) {
        notificationSender.sendNotification(content);
    }
}
