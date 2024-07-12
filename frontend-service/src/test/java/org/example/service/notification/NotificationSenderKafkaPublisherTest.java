package org.example.service.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NotificationSenderKafkaPublisherTest {

    private NotificationSenderKafkaPublisher notificationSenderKafkaPublisher;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationSenderKafkaPublisher = new NotificationSenderKafkaPublisher(kafkaTemplate);
    }

    @Test
    void shouldCashNotificationsWhenSendNotification() {

        var messagesExpected = new LinkedHashSet<String>();

        for (int i = 1; i <= 10; i++) {
            var notification = "notification: " + i;
            notificationSenderKafkaPublisher.sendNotification(notification);
            messagesExpected.add(notification);
        }

        var messagesOfNotificationSender = getMessagesOfNotificationSender(notificationSenderKafkaPublisher);
        assertEquals(messagesExpected, messagesOfNotificationSender);

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void shouldCashNothingWhenSendNotification() {

        var notification = "";
        notificationSenderKafkaPublisher.sendNotification(notification);

        var messagesOfNotificationSender = getMessagesOfNotificationSender(notificationSenderKafkaPublisher);
        assertTrue(messagesOfNotificationSender.isEmpty());

        verify(kafkaTemplate, never()).send(any(), any(), any());
    }

    @Test
    void shouldSendCashedNotificationsWhenSendNotificationsToKafka() {

        var messagesOfNotificationSender = getMessagesOfNotificationSender(notificationSenderKafkaPublisher);
        var messagesBeforeSending = new LinkedHashSet<String>();

        for (int i = 1; i <= 10; i++) {
            var notification = "notification: " + i;
            messagesOfNotificationSender.add(notification);
            messagesBeforeSending.add(notification);
        }

        notificationSenderKafkaPublisher.sendNotificationsToKafka();

        assertTrue(messagesOfNotificationSender.isEmpty());

        for (var message : messagesBeforeSending) {
            verify(kafkaTemplate, times(1)).send(null, "key", message);
        }

        verify(kafkaTemplate, times(messagesBeforeSending.size())).send(isNull(), anyString(), anyString());
    }

    @Test
    void shouldSendNothingWhenSendNotificationsToKafka() {
        notificationSenderKafkaPublisher.sendNotificationsToKafka();
        verify(kafkaTemplate, never()).send(isNull(), anyString(), anyString());
    }

    private static Set<String> getMessagesOfNotificationSender(NotificationSenderKafkaPublisher notificationSender) {
        try {
            var field = getFieldMessagesOfNotificationSender();
            field.trySetAccessible();
            var valueOfField = field.get(notificationSender);
            //noinspection unchecked
            return (Set<String>) valueOfField;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getFieldMessagesOfNotificationSender() throws NoSuchFieldException {
            return NotificationSenderKafkaPublisher.class.getDeclaredField("messages");
    }
}