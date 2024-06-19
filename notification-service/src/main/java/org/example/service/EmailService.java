package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void sendSimpleEmail(String emailTo, String subject, String body) {

        var message = new SimpleMailMessage();

        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(body);

        log.info("Trying to send email: {}", message);

        try {
            mailSender.send(message);
            log.info("Email was sent successfully: {}", message);
        } catch (MailException e) {
            log.error("Sending email FAILED!!! - {}\n{}", message, e.getMessage());
            throw e;
        }
    }
}
