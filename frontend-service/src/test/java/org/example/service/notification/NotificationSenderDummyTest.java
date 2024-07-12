package org.example.service.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotificationSenderDummyTest {

    @Autowired
    private NotificationService notificationService;

    @Test
    void shouldSendNotificationWhenSendNotification() {
        var notification = "hello";
        notificationService.sendNotification(notification);
    }

    @Test
    void shouldSendNothingWhenSendNotification() {
        var notification = "";
        notificationService.sendNotification(notification);
    }
}