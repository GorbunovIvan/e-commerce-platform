package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.example.model.dto.OrderDTO;
import org.example.model.dto.StatusTrackerRecordDTO;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class MessagesParserTest {

    @Autowired
    private MessagesParser messagesParser;

    @SpyBean
    private ObjectMapper objectMapper;

    private final EasyRandom easyRandom = new EasyRandom();

    @PostConstruct
    void init() {
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldReturnOrderDTOWhenParseToObject() throws IOException {

        var order = easyRandom.nextObject(OrderDTO.class);
        var message = objectMapper.writeValueAsBytes(order);

        var orderReceived = messagesParser.parseToObject(message, OrderDTO.class);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);

        verify(objectMapper, times(1)).readValue(message, OrderDTO.class);
    }

    @Test
    void shouldReturnStatusRecordWhenParseToObject() throws IOException {

        var statusRecord = easyRandom.nextObject(StatusTrackerRecordDTO.class);
        var message = objectMapper.writeValueAsBytes(statusRecord);

        var statusRecordReceived = messagesParser.parseToObject(message, StatusTrackerRecordDTO.class);
        assertNotNull(statusRecordReceived);
        assertEquals(statusRecord, statusRecordReceived);

        verify(objectMapper, times(1)).readValue(message, StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldThrowExceptionWhenParseToObject() {
        assertThrows(Exception.class, () -> messagesParser.parseToObject(null, StatusTrackerRecordDTO.class));
        assertThrows(Exception.class, () -> messagesParser.parseToObject(new byte[99], StatusTrackerRecordDTO.class));
    }
}