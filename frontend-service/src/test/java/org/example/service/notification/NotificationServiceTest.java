package org.example.service.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private NotificationSender notificationSender;

    @Test
    void shouldSendNotificationWhenSendNotification() {
        var notification = "Hello";
        notificationService.sendNotification(notification);
        verify(notificationSender, times(1)).sendNotification(notification);
    }
}