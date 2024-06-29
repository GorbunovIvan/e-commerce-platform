package org.example.repository.orders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderRepositoryDummy implements OrderRepository {

    private final StatusTrackerRecordRepositoryDummy statusTrackerRecordRepository;

    private final List<Order> orders = new ArrayList<>();

    @Override
    public Order getById(String id) {
        log.info("Searching for order with id={}", id);
        return orders.stream()
                .filter(order -> Objects.equals(order.getId(), id))
                .peek(this::fillCurrentStatusToOrder)
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Order> getByIds(Set<String> ids) {
        log.info("Searching for orders with ids={}", ids);
        var ordersFound = orders.stream()
                .filter(order -> ids.contains(order.getId()))
                .toList();
        fillCurrentStatusesToOrders(ordersFound);
        return ordersFound;
    }

    @Override
    public List<Order> getAll() {
        log.info("Searching for all orders");
        var ordersFound = new ArrayList<>(orders);
        Collections.sort(ordersFound);
        fillCurrentStatusesToOrders(ordersFound);
        return ordersFound;
    }

    @Override
    public List<Order> getAllByUser(Long userId) {
        log.info("Searching for orders with userId={}", userId);
        var ordersFound = orders.stream()
                .filter(order -> order.getUser() != null)
                .filter(order -> Objects.equals(order.getUser().getId(), userId))
                .sorted()
                .toList();
        fillCurrentStatusesToOrders(ordersFound);
        return ordersFound;
    }

    @Override
    public List<Order> getAllByProduct(Long productId) {
        log.info("Searching for orders with productId={}", productId);
        var ordersFound = orders.stream()
                .filter(order -> order.getProduct() != null)
                .filter(order -> Objects.equals(order.getProduct().getId(), productId))
                .sorted()
                .toList();
        fillCurrentStatusesToOrders(ordersFound);
        return ordersFound;
    }

    @Override
    public Order create(Order order) {
        log.info("Creating order '{}'", order);
        var nextId = nextId();
        order.setId(nextId);
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(LocalDateTime.now());
        }
        orders.add(order);
        statusTrackerRecordRepository.updateStatusForOrder(order, Status.CREATED);
        return order;
    }

    @Override
    public synchronized Order update(String id, Order order) {

        log.info("Updating order with id={}, {}", id, order);

        var orderExisting = getById(id);
        if (orderExisting == null) {
            log.error("Order with id {} not found", id);
            return null;
        }

        if (order.getUser() != null) {
            orderExisting.setUser(order.getUser());
        }
        if (order.getProduct() != null) {
            orderExisting.setProduct(order.getProduct());
        }
        if (order.getCreatedAt() != null) {
            orderExisting.setCreatedAt(order.getCreatedAt());
        }

        fillCurrentStatusToOrder(orderExisting);
        return orderExisting;
    }

    @Override
    public Order changeOrderStatus(String id, Status status) {

        log.info("Updating status for order with id={}, new status={}", id, status);

        var order = getById(id);
        if (order == null) {
            log.error("Order with id {} not found", id);
            return null;
        }

        return statusTrackerRecordRepository.updateStatusForOrder(order, status);
    }

    @Override
    public synchronized void deleteById(String id) {
        log.warn("Deleting order by id={}", id);
        var indexOfOrderInList = getIndexOfOrderInListById(id);
        if (indexOfOrderInList == -1) {
            return;
        }
        orders.remove(indexOfOrderInList);
    }

    private String nextId() {

        var ids = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        while (true) {
            var nextId = String.valueOf(new Random().nextLong());
            if (!ids.contains(nextId)) {
                return nextId;
            }
        }
    }

    private int getIndexOfOrderInListById(String id) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void fillCurrentStatusToOrder(Order order) {
        var status = statusTrackerRecordRepository.getCurrentStatusOfOrder(order.getId());
        order.setStatus(status);
    }

    private void fillCurrentStatusesToOrders(List<Order> orders) {

        if (orders.isEmpty()) {
            return;
        }

        var orderIds = orders.stream()
                .map(Order::getId)
                .distinct()
                .toList();

        var ordersAndStatusesMap = statusTrackerRecordRepository.getCurrentStatusesOfOrders(orderIds);

        for (var order : orders) {
            var status = ordersAndStatusesMap.get(order);
            if (status != null) {
                order.setStatus(status);
            } else {
                log.warn("Status of order with id={} not found", order.getId());
            }
        }
    }
}
