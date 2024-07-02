package org.example.repository.orders.remote;

import lombok.RequiredArgsConstructor;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.repository.orders.OrderRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Primary
@ConditionalOnProperty(name = "order-service.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OrderRepositoryFacade implements OrderRepository {

    private final OrderRepositoryGraphQL orderRepositoryGraphQL;
    private final OrderRepositoryRabbitMQPublisher orderRepositoryRabbitMQ;

    @Override
    public Order getById(String id) {
        return orderRepositoryGraphQL.getById(id);
    }

    @Override
    public List<Order> getByIds(Set<String> ids) {
        return orderRepositoryGraphQL.getByIds(ids);
    }

    @Override
    public List<Order> getAll() {
        return orderRepositoryGraphQL.getAll();
    }

    @Override
    public List<Order> getAllByUser(Long userId) {
        return orderRepositoryGraphQL.getAllByUser(userId);
    }

    @Override
    public List<Order> getAllByProduct(Long productId) {
        return orderRepositoryGraphQL.getAllByProduct(productId);
    }

    @Override
    public Order create(Order order) {
        return orderRepositoryRabbitMQ.create(order);
    }

    @Override
    public Order update(String id, Order order) {
        return orderRepositoryRabbitMQ.update(id, order);
    }

    @Override
    public Order changeOrderStatus(String id, Status status) {
        return orderRepositoryRabbitMQ.changeOrderStatus(id, status);
    }

    @Override
    public void deleteById(String id) {
        orderRepositoryRabbitMQ.deleteById(id);
    }
}
