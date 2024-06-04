package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.model.Status;
import org.example.model.dto.OrderDTO;
import org.example.repository.OrderRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final StatusTrackerRecordService statusTrackerRecordService;
    private final MessagesParser messagesParser;

    public Order getById(String id) {
        log.info("Searching for order with id={}", id);
        var orderOptional = orderRepository.findById(id);
        orderOptional.ifPresent(this::fillCurrentStatusToOrder);
        return orderOptional.orElse(null);
    }

    public List<Order> getAll() {
        log.info("Searching for all orders");
        var orders = orderRepository.findAll();
        fillCurrentStatusesToOrders(orders);
        return orders;
    }

    public List<Order> getAllByUser(Long userId) {
        log.info("Searching for orders with userId={}", userId);
        var orders = orderRepository.findAllByUserId(userId);
        fillCurrentStatusesToOrders(orders);
        return orders;
    }

    public List<Order> getAllByProduct(Long productId) {
        log.info("Searching for orders with productId={}", productId);
        var orders = orderRepository.findAllByProductId(productId);
        fillCurrentStatusesToOrders(orders);
        return orders;
    }

    public Order create(byte[] orderMessage) {
        var orderDTO = messagesParser.parseOrderFromMessage(orderMessage);
        return create(orderDTO);
    }

    public Order create(String orderMessage) {
        var orderDTO = messagesParser.parseOrderFromMessage(orderMessage);
        return create(orderDTO);
    }

    public Order create(@NonNull OrderDTO orderDTO) {

        log.info("Creating order '{}'", orderDTO);

        var order = orderDTO.toOrder();
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        order = orderRepository.save(order);

        statusTrackerRecordService.updateStatusForOrder(order, Status.CREATED);

        return order;
    }

    @Transactional
    public Order update(String id, @NonNull OrderDTO orderDTO) {

        log.info("Updating order with id={}, {}", id, orderDTO);

        var orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            log.error("Order with id {} not found", id);
            return null;
        }

        var order = orderOptional.get();

        if (orderDTO.getUserId() != null) {
            order.setUserId(orderDTO.getUserId());
        }
        if (orderDTO.getProductId() != null) {
            order.setProductId(orderDTO.getProductId());
        }
        if (orderDTO.getCreatedAt() != null) {
            order.setCreatedAt(orderDTO.getCreatedAt());
        }

        order = orderRepository.save(order);
        fillCurrentStatusToOrder(order);
        return order;
    }

    public Order changeOrderStatus(String id, @NonNull Status status) {

        log.info("Updating status for order with id={}, new status={}", id, status);

        var orderOptional = orderRepository.findById(id);
        if (orderOptional.isEmpty()) {
            log.error("Order with id {} not found", id);
            return null;
        }
        var order = orderOptional.get();

        return statusTrackerRecordService.updateStatusForOrder(order, status);
    }

    public void deleteById(String id) {
        log.warn("Deleting order by id={}", id);
        orderRepository.deleteById(id);
    }

    private void fillCurrentStatusToOrder(@NonNull Order order) {
        var status = statusTrackerRecordService.getCurrentStatusOfOrder(order.getId());
        order.setStatus(status);
    }

    private void fillCurrentStatusesToOrders(@NonNull List<Order> orders) {

        if (orders.isEmpty()) {
            return;
        }

        var orderIds = orders.stream()
                .map(Order::getId)
                .distinct()
                .toList();

        var ordersAndStatusesMap = statusTrackerRecordService.getCurrentStatusesOfOrders(orderIds);

        for (var order : orders) {
            var status = ordersAndStatusesMap.get(order.getId());
            if (status != null) {
                order.setStatus(status);
            } else {
                log.warn("Status for order with id={} not found", order.getId());
            }
        }
    }
}
