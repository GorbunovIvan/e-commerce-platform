package org.example.service.orders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.NotFoundException;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.example.repository.orders.OrderRepositoryDummy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepositoryDummy orderRepository;

    public Order getById(String id) {
        log.info("Searching for order with id={}", id);
        return orderRepository.getById(id);
    }

    public List<Order> getAll() {
        log.info("Searching for all orders");
        return orderRepository.getAll();
    }

    public List<Order> getAllByUser(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return this.getAllByUser(user.getId());
    }

    public List<Order> getAllByUser(Long userId) {
        log.info("Searching for orders with userId={}", userId);
        return orderRepository.getAllByUser(userId);
    }

    public List<Order> getAllByProduct(Product product) {
        if (product == null) {
            return Collections.emptyList();
        }
        return this.getAllByProduct(product.getId());
    }

    public List<Order> getAllByProduct(Long productId) {
        log.info("Searching for orders with productId={}", productId);
        return orderRepository.getAllByProduct(productId);
    }

    public Order create(Order order) {
        log.info("Creating order '{}'", order);
        return orderRepository.create(order);
    }

    public Order update(String id, Order order) {
        log.info("Updating order with id={}, {}", id, order);
        var orderUpdated = orderRepository.update(id, order);
        if (orderUpdated == null) {
            throw new NotFoundException(String.format("Order with id=%s not found", id));
        }
        return orderUpdated;
    }

    public Order changeOrderStatus(String id, Status status) {
        log.info("Updating status for order with id={}, new status={}", id, status);
        return orderRepository.changeOrderStatus(id, status);
    }

    public void deleteById(String id) {
        log.warn("Deleting order by id={}", id);
        orderRepository.deleteById(id);
    }
}
