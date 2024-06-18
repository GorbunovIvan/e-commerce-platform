package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class NotificationService {

    public void sendToEmail(Collection<String> notifications) {
        log.info("Sending email to {} with message {}", "abcd@mail.com", notifications);
    }
}
