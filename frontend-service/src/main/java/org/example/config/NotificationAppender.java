package org.example.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import org.example.service.notification.NotificationService;

@Setter
public class NotificationAppender extends AppenderBase<ILoggingEvent> {

    private NotificationService notificationService;

    @Override
    protected void append(ILoggingEvent eventObject) {
        String logMessage = eventObject.getFormattedMessage();
        if (this.notificationService != null) {
            this.notificationService.sendNotification(logMessage);
        }
    }
}
