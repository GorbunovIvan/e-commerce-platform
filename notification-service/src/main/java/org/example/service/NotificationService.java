package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;

    @Value("${mail.receiver}")
    private String receiver;

    public void sendToEmail(Collection<String> notifications) {

        log.info("Sending notification to email {}, message - {}", receiver, notifications);

        var subject = "Notifications from E-COMMERCE PLATFORM";
        var body = notifications.toString();

        emailService.sendSimpleEmail(receiver, subject, body);
    }
}
