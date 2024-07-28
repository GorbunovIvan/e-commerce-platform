package org.example.config;

import ch.qos.logback.classic.Logger;
import jakarta.annotation.PostConstruct;
import org.example.service.notification.NotificationService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogbackConfiguration {

//    private final NotificationService notificationService;
//
//    /**
//     * We need to take logger appender (out custom "NotificationAppender")
//     * and add the bean "NotificationService" to it via its setter,
//     * so that this appender can send the logs to notificationService as notifications
//     */
//    @PostConstruct
//    public void configureLogbackAppender() {
//        var logger = getNotificationLogger();
//        var notificationAppender = (NotificationAppender) logger.getAppender("NOTIFICATION");
//        if (notificationAppender == null) {
//            System.out.println("No notification appender found in '" + logger.getName() + "' logger!");
//            return;
//        }
//        notificationAppender.setNotificationService(notificationService);
//    }
//
//    private Logger getNotificationLogger() {
//        var loggerName = Logger.ROOT_LOGGER_NAME;
//        return (Logger) LoggerFactory.getLogger(loggerName);
//    }
}