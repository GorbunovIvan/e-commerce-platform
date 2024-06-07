package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.IdAndOrderDTO;
import org.example.model.dto.IdAndStatusDTO;
import org.example.model.dto.OrderDTO;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final OrderService orderService;
    private final MessagesParser messagesParser;

    @RabbitListener(queues = "${spring.rabbitmq.queues.order-create}")
    public void processOrderCreateQueue(Message message) {
        log.info("Received message for creating order: {}", message.getMessageProperties());
        var objectRetrieved = messagesParser.parseToObject(message.getBody(), OrderDTO.class);
        log.info("Message body: {}", objectRetrieved);
        orderService.create(objectRetrieved);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.order-update}")
    public void processOrderUpdateQueue(Message message) {
        log.info("Received message for updating order: {}", message.getMessageProperties());
        var objectRetrieved = messagesParser.parseToObject(message.getBody(), IdAndOrderDTO.class);
        log.info("Message body: {}", objectRetrieved);
        orderService.update(objectRetrieved.getId(), objectRetrieved.getOrderDTO());
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.order-change-status}")
    public void processOrderChangeStatusQueue(Message message) {
        log.info("Received message for changing order status: {}", message.getMessageProperties());
        var objectRetrieved = messagesParser.parseToObject(message.getBody(), IdAndStatusDTO.class);
        log.info("Message body: {}", objectRetrieved);
        orderService.changeOrderStatus(objectRetrieved.getId(), objectRetrieved.getStatus());
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.order-delete}")
    public void processOrderDeleteQueue(Message message) {
        log.info("Received message for deleting order: {}", message.getMessageProperties());
        var objectRetrieved = messagesParser.parseToObject(message.getBody(), String.class);
        log.info("Message body: {}", objectRetrieved);
        orderService.deleteById(objectRetrieved);
    }
}
