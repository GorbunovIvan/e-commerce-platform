package org.example.repository.orders.remote;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.dto.IdAndOrderRequestDTO;
import org.example.model.orders.dto.IdAndStatusDTO;
import org.example.model.orders.dto.OrderRequestDTO;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = "order-service.enabled=true")
class OrderRepositoryRabbitMQPublisherTest {

    @Autowired
    private OrderRepositoryRabbitMQPublisher orderRepositoryRabbitMQPublisher;

    @MockBean
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private TopicExchange topicExchange;

    @Value("${spring.rabbitmq.queues.order-create}")
    private String queueOrderCreate;
    @Value("${spring.rabbitmq.queues.order-update}")
    private String queueOrderUpdate;
    @Value("${spring.rabbitmq.queues.order-change-status}")
    private String queueOrderChangeStatus;
    @Value("${spring.rabbitmq.queues.order-delete}")
    private String queueOrderDelete;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldSendMessageToRabbitWhenCreate() {

        var order = easyRandom.nextObject(Order.class);
        var orderDTO = OrderRequestDTO.fromOrder(order);

        orderRepositoryRabbitMQPublisher.create(order);
        verify(rabbitTemplate, times(1)).convertAndSend(topicExchange.getName(), queueOrderCreate, orderDTO);
    }

    @Test
    void shouldSendMessageToRabbitWhenUpdate() {

        var order = easyRandom.nextObject(Order.class);
        var message = new IdAndOrderRequestDTO(order.getId(), OrderRequestDTO.fromOrder(order));

        orderRepositoryRabbitMQPublisher.update(order.getId(), order);
        verify(rabbitTemplate, times(1)).convertAndSend(topicExchange.getName(), queueOrderUpdate, message);
    }

    @Test
    void shouldSendMessageToRabbitWhenChangeOrderStatus() {

        var id = "87";
        var status = Status.IN_A_WAY;

        var message = new IdAndStatusDTO(id, status);

        orderRepositoryRabbitMQPublisher.changeOrderStatus(id, status);
        verify(rabbitTemplate, times(1)).convertAndSend(topicExchange.getName(), queueOrderChangeStatus, message);
    }

    @Test
    void shouldSendMessageToRabbitWhenDeleteById() {
        var id = "23";
        orderRepositoryRabbitMQPublisher.deleteById(id);
        verify(rabbitTemplate, times(1)).convertAndSend(topicExchange.getName(), queueOrderDelete, id);
    }
}