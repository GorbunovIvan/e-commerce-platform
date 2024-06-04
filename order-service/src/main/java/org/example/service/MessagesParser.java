package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.OrderDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesParser {

    private final ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        objectMapper.findAndRegisterModules();
    }

    OrderDTO parseOrderFromMessage(String message) {
        try {
            log.info("Parsing order from json: {}", message);
            return objectMapper.readValue(message, OrderDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    OrderDTO parseOrderFromMessage(byte[] message) {
        try {
            log.info("Parsing order from bytes");
            return objectMapper.readValue(message, OrderDTO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
