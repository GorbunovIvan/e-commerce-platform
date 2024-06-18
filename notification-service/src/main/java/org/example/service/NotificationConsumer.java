package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.notifications-topic}")
    public void messageListener(List<String> messages) {
        try {
            log.info("Consuming: {}", messages);
            notificationService.sendToEmail(messages);
            log.info("Consumed: {}", messages);
        } catch (Exception e) {
            log.error("Consuming failed: {}", messages);
            log.error(e.getMessage());
        }
    }
}
