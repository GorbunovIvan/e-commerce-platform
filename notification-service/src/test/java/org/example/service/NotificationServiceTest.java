package org.example.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

@SpringBootTest
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private EmailService emailService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Value("${mail.receiver}")
    private String receiver;

    @Test
    void shouldSendEmailWhenSendToEmail() {

        var notifications = easyRandom.objects(String.class, 10).toList();

        var subject = "Notifications from E-COMMERCE PLATFORM";
        var body = notifications.toString();

        notificationService.sendToEmail(notifications);

        verify(emailService, only()).sendSimpleEmail(receiver, subject, body);
        verify(emailService, times(1)).sendSimpleEmail(receiver, subject, body);
    }
}