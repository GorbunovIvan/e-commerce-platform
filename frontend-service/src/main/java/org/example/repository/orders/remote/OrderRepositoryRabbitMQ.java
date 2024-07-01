package org.example.repository.orders.remote;

import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "order-service.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class OrderRepositoryRabbitMQ {

    public Order create(Order order) {
        log.info("Creating order '{}'", order);
        return null;
    }

    public Order update(String id, Order order) {
        log.info("Updating order with id={}, {}", id, order);
        return null;
    }
    public Order changeOrderStatus(String id, Status status) {
        log.info("Updating status for order with id={}, new status={}", id, status);
        return null;
    }

    public void deleteById(String id) {
        log.warn("Deleting order by id={}", id);
    }
}
