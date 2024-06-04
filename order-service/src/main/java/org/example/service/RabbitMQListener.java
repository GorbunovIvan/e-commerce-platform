package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final OrderService orderService;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void processMyQueue(Message message) {
        log.info("Received message: {}", message.getMessageProperties());
        orderService.create(message.getBody());
    }
}
