package org.example.config;

import org.example.model.dto.IdAndOrderDTO;
import org.example.model.dto.IdAndStatusDTO;
import org.example.model.dto.OrderDTO;
import org.example.model.dto.StatusTrackerRecordDTO;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        System.setProperty("spring.amqp.deserialization.trust.all", "true");
        return new CachingConnectionFactory(rabbitmqHost);
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(DefaultClassMapper classMapper) {
        var converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper() {

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("org.example.model.orders.dto.OrderRequestDTO", OrderDTO.class);
        idClassMapping.put("org.example.model.orders.dto.IdAndStatusDTO", IdAndStatusDTO.class);
        idClassMapping.put("org.example.model.orders.dto.IdAndOrderRequestDTO", IdAndOrderDTO.class);
        idClassMapping.put("org.example.model.orders.dto.StatusTrackerRecordDTO", StatusTrackerRecordDTO.class);

        var classMapper = new DefaultClassMapper();
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
