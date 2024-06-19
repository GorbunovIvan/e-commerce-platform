package org.example.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender javaMailSender;
    
    private final EasyRandom easyRandom = new EasyRandom();

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Test
    void shouldSendEmailWhenSendSimpleEmail() {

        var emailTo = easyRandom.nextObject(String.class);
        var emailSubject = easyRandom.nextObject(String.class);
        var emailBody = easyRandom.nextObject(String.class);

        emailService.sendSimpleEmail(emailTo, emailSubject, emailBody);

        verify(javaMailSender, only()).send(any(SimpleMailMessage.class));

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals(emailFrom, message.getFrom());
        assertEquals(emailSubject, message.getSubject());
        assertEquals(emailBody, message.getText());
        assertNotNull(message.getTo());
        assertEquals(1, message.getTo().length);
        assertEquals(emailTo, message.getTo()[0]);
    }

    @Test
    void shouldThrowExceptionWhenSendSimpleEmail() {

        doThrow(MailSendException.class).when(javaMailSender).send(any(SimpleMailMessage.class));

        var emailTo = easyRandom.nextObject(String.class);
        var emailSubject = easyRandom.nextObject(String.class);
        var emailBody = easyRandom.nextObject(String.class);

        assertThrows(MailSendException.class, () -> emailService.sendSimpleEmail(emailTo, emailSubject, emailBody));

        verify(javaMailSender, only()).send(any(SimpleMailMessage.class));
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}