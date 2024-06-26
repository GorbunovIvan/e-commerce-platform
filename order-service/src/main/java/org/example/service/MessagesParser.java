package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesParser {

    private final ObjectMapper objectMapper;

    @PostConstruct
    void init() {
        objectMapper.findAndRegisterModules();
    }

    public <T> T parseToObject(byte[] message, @NonNull Class<T> clazz) {
        try {
            log.info("Parsing message from bytes to type {}", clazz.getName());
            return objectMapper.readValue(message, clazz);
        } catch (Exception e) {
            log.error("Parsing message from bytes to type {} failed - {}", clazz.getName(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
