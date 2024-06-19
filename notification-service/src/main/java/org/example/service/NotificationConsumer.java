package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "consumer.enabled", havingValue = "true")
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
        } catch (MailSendException e) {
            log.error("Consuming was rolled back due to mail sending errors: {}", messages);
            throw e;
        } catch (Exception e) {
            log.error("Consuming failed and was rolled back: {}", messages);
            log.error(e.getMessage());
            throw e;
        }
    }
}
