package org.example.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Using "System.out.println" because this service can be used
 * to send all the logged messages (intercepting them), which will cause recursion
 */
@Service
@Primary
@ConditionalOnProperty(name = "notification-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@EnableScheduling
public class NotificationSenderKafkaPublisher implements NotificationSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.notifications-topic}")
    private String notificationsTopic;

    private final Set<String> messages = new LinkedHashSet<>();

    @Override
    public void sendNotification(String message) {
        // We will cache messages first, otherwise Kafka for some reason stumbles and freezes
        if (message == null || message.isEmpty()) {
            return;
        }
        messages.add(message);
    }

    // Publishing messages to Kafka
    @Scheduled(fixedRate = 5000)
    public void sendNotificationsToKafka() {

        var iterator = messages.iterator();
        while (iterator.hasNext()) {

            var message = iterator.next();

            System.out.println("Publishing message to Kafka: " + message);

            try {
                var future = kafkaTemplate.send(notificationsTopic, "key", message);
                future.thenAccept(result -> System.out.println("Message published: " + result));
                kafkaTemplate.flush();
            } catch (Exception e) {
                System.out.println("Failed to publish message ('" + message + "'). " + e.getMessage());
            }

            iterator.remove();
        }
    }
}
