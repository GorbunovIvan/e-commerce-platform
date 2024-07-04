package org.example.service.notification;

import org.springframework.stereotype.Service;

@Service
public class NotificationSenderDummy implements NotificationSender {

    /**
     * Using "System.out.println" because this service can be used
     * to send all the logged messages (intercepting them), which will cause recursion
     */
    @Override
    public void sendNotification(String message) {
        if (message == null || message.isEmpty()) {
            System.out.println("Notification message is empty");
            return;
        }
        System.out.println("Sending notification: " + message);
    }
}
