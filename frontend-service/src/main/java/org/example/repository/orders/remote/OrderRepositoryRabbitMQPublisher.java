package org.example.repository.orders.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.dto.IdAndOrderRequestDTO;
import org.example.model.orders.dto.IdAndStatusDTO;
import org.example.model.orders.Status;
import org.example.model.orders.dto.OrderRequestDTO;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "order-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryRabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final TopicExchange topicExchange;

    @Value("${spring.rabbitmq.queues.order-create}")
    private String queueOrderCreate;

    @Value("${spring.rabbitmq.queues.order-update}")
    private String queueOrderUpdate;

    @Value("${spring.rabbitmq.queues.order-change-status}")
    private String queueOrderChangeStatus;

    @Value("${spring.rabbitmq.queues.order-delete}")
    private String queueOrderDelete;

    public Order create(Order order) {
        log.info("Sending message for order creation: {}", order);
        var orderDTO = OrderRequestDTO.fromOrder(order);
        rabbitTemplate.convertAndSend(topicExchange.getName(), queueOrderCreate, orderDTO);
        log.info("Order creation message sent to RabbitMQ");
        return null;
    }

    public Order update(String id, Order order) {
        log.info("Sending message for order updating: id='{}' - order={}", id, order);
        var orderDTO = OrderRequestDTO.fromOrder(order);
        var message = new IdAndOrderRequestDTO(id, orderDTO);
        rabbitTemplate.convertAndSend(topicExchange.getName(), queueOrderUpdate, message);
        log.info("Order update message sent to RabbitMQ");
        return null;
    }

    public Order changeOrderStatus(String id, Status status) {
        log.info("Sending message for changing order status: id='{}' - status={}", id, status);
        var message = new IdAndStatusDTO(id, status);
        rabbitTemplate.convertAndSend(topicExchange.getName(), queueOrderChangeStatus, message);
        log.info("Order change-status message sent to RabbitMQ");
        return null;
    }

    public void deleteById(String id) {
        log.info("Sending message for order deleting: id='{}'", id);
        rabbitTemplate.convertAndSend(topicExchange.getName(), queueOrderDelete, id);
        log.info("Order delete message sent to RabbitMQ");
    }
}
