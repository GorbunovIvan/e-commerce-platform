package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.example.model.dto.IdAndOrderDTO;
import org.example.model.dto.IdAndStatusDTO;
import org.example.model.dto.OrderDTO;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RabbitMQListenerTest {

    @Autowired
    private RabbitMQListener rabbitMQListener;

    @MockBean
    private OrderService orderService;
    @MockBean
    private MessagesParser messagesParser;

    @Autowired
    private ObjectMapper objectMapper;

    private final EasyRandom easyRandom = new EasyRandom();

    @PostConstruct
    void init() {
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldProcessMessageWhenProcessOrderCreateQueue() throws JsonProcessingException {

        var orderDTO = easyRandom.nextObject(OrderDTO.class);
        var orderDTOBytes = objectMapper.writeValueAsBytes(orderDTO);

        var message = new Message(orderDTOBytes);

        when(messagesParser.parseToObject(any(), any())).thenReturn(orderDTO);

        rabbitMQListener.processOrderCreateQueue(message);

        verify(messagesParser, times(1)).parseToObject(orderDTOBytes, OrderDTO.class);
        verify(orderService, times(1)).create(orderDTO);
    }

    @Test
    void shouldProcessMessageWhenProcessOrderUpdateQueue() throws JsonProcessingException {

        var idAndOrderDTO = easyRandom.nextObject(IdAndOrderDTO.class);
        var idAndOrderDTOBytes = objectMapper.writeValueAsBytes(idAndOrderDTO);

        var message = new Message(idAndOrderDTOBytes);

        when(messagesParser.parseToObject(any(), any())).thenReturn(idAndOrderDTO);

        rabbitMQListener.processOrderUpdateQueue(message);

        verify(messagesParser, times(1)).parseToObject(idAndOrderDTOBytes, IdAndOrderDTO.class);
        verify(orderService, times(1)).update(idAndOrderDTO.getId(), idAndOrderDTO.getOrderDTO());
    }

    @Test
    void shouldProcessMessageWhenProcessChangeStatusQueue() throws JsonProcessingException {

        var idAndStatusDTO = easyRandom.nextObject(IdAndStatusDTO.class);
        var idAndStatusDTOBytes = objectMapper.writeValueAsBytes(idAndStatusDTO);

        var message = new Message(idAndStatusDTOBytes);

        when(messagesParser.parseToObject(any(), any())).thenReturn(idAndStatusDTO);

        rabbitMQListener.processOrderChangeStatusQueue(message);

        verify(messagesParser, times(1)).parseToObject(idAndStatusDTOBytes, IdAndStatusDTO.class);
        verify(orderService, times(1)).changeOrderStatus(idAndStatusDTO.getId(), idAndStatusDTO.getStatus());
    }

    @Test
    void processOrderDeleteQueue() throws JsonProcessingException {

        var id = "987";
        var idBytes = objectMapper.writeValueAsBytes(id);

        var message = new Message(idBytes);

        when(messagesParser.parseToObject(any(), any())).thenReturn(id);

        rabbitMQListener.processOrderDeleteQueue(message);

        verify(messagesParser, times(1)).parseToObject(idBytes, String.class);
        verify(orderService, times(1)).deleteById(id);
    }
}