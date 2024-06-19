package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueuesConfig {

    @Value("${spring.rabbitmq.topicExchange}")
    private String topicExchangeName;

    @Value("${spring.rabbitmq.queues.order-create}")
    private String queueOrderCreate;

    @Value("${spring.rabbitmq.queues.order-update}")
    private String queueOrderUpdate;

    @Value("${spring.rabbitmq.queues.order-change-status}")
    private String queueOrderChangeStatus;

    @Value("${spring.rabbitmq.queues.order-delete}")
    private String queueOrderDelete;

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    public Queue orderCreateQueue() {
        return new Queue(queueOrderCreate);
    }

    @Bean
    public Queue orderUpdateQueue() {
        return new Queue(queueOrderUpdate);
    }

    @Bean
    public Queue orderChangeStatusQueue() {
        return new Queue(queueOrderChangeStatus);
    }

    @Bean
    public Queue orderDeleteQueue() {
        return new Queue(queueOrderDelete);
    }

    @Bean
    public Binding orderCreateBinding(TopicExchange topicExchange, Queue orderCreateQueue) {
        return BindingBuilder.bind(orderCreateQueue).to(topicExchange).with(orderCreateQueue.getName());
    }

    @Bean
    public Binding orderUpdateBinding(TopicExchange topicExchange, Queue orderUpdateQueue) {
        return BindingBuilder.bind(orderUpdateQueue).to(topicExchange).with(orderUpdateQueue.getName());
    }

    @Bean
    public Binding orderChangeStatusBinding(TopicExchange topicExchange, Queue orderChangeStatusQueue) {
        return BindingBuilder.bind(orderChangeStatusQueue).to(topicExchange).with(orderChangeStatusQueue.getName());
    }

    @Bean
    public Binding orderDeleteBinding(TopicExchange topicExchange, Queue orderDeleteQueue) {
        return BindingBuilder.bind(orderDeleteQueue).to(topicExchange).with(orderDeleteQueue.getName());
    }
}
