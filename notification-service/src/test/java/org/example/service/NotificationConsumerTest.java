package org.example.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    // We can't autowire it because this class is conditional on a property that is disabled in the test environment
    private NotificationConsumer notificationConsumer;

    @Mock
    private NotificationService notificationService;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        Mockito.reset(notificationService);
        this.notificationConsumer = new NotificationConsumer(notificationService);
    }

    @Test
    void shouldConsumeMessagesWhenMessageListener() {
        var messages = easyRandom.objects(String.class, 7).toList();
        notificationConsumer.messageListener(messages);
        verify(notificationService, times(1)).sendToEmail(messages);
    }

    @Test
    void shouldThrowMailSendExceptionWhenMessageListener() {
        doThrow(MailSendException.class).when(notificationService).sendToEmail(any());
        var messages = easyRandom.objects(String.class, 7).toList();
        assertThrows(MailSendException.class, () -> notificationConsumer.messageListener(messages));
        verify(notificationService, times(1)).sendToEmail(messages);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenMessageListener() {
        doThrow(RuntimeException.class).when(notificationService).sendToEmail(any());
        var messages = easyRandom.objects(String.class, 7).toList();
        assertThrows(RuntimeException.class, () -> notificationConsumer.messageListener(messages));
        verify(notificationService, times(1)).sendToEmail(messages);
    }
}