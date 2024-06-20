package org.example.repository.orders;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderRepository {
    Order getById(String id);
    List<Order> getAll();
    List<Order> getAllByUser(Long userId);
    List<Order> getAllByProduct(Long productId);
    Order create(Order order);
    Order update(String id, Order order);
    Order changeOrderStatus(String id, Status status);
    void deleteById(String id);
}
