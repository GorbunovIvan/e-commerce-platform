package org.example.service;

import org.example.model.Order;
import org.example.model.Status;
import org.example.model.dto.OrderDTO;
import org.example.repository.OrderRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private StatusTrackerRecordService statusTrackerRecordService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnOrderWhenGetById() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));
        when(statusTrackerRecordService.getCurrentStatusOfOrder(id)).thenReturn(Status.IN_PROGRESS);

        var orderReceived = orderService.getById(id);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);

        verify(orderRepository, times(1)).findById(id);
        verify(statusTrackerRecordService, times(1)).getCurrentStatusOfOrder(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "999";

        when(orderRepository.findById(anyString())).thenReturn(Optional.empty());

        var orderReceived = orderService.getById(id);
        assertNull(orderReceived);

        verify(orderRepository, times(1)).findById(id);
        verify(statusTrackerRecordService, never()).getCurrentStatusOfOrder(anyString());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetByIds() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        var ordersIds = orders.stream().map(Order::getId).toList();

        when(orderRepository.findAllByIdIn(ordersIds)).thenReturn(orders);

        var ordersReceived = orderService.getByIds(ordersIds);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).findAllByIdIn(ordersIds);
        verify(statusTrackerRecordService, times(1)).getCurrentStatusesOfOrders(ordersIds);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var ordersIds = easyRandom.objects(String.class, 4).toList();

        when(orderRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getByIds(ordersIds);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).findAllByIdIn(ordersIds);
        verify(statusTrackerRecordService, never()).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAll() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        var orderIds = orders.stream().map(Order::getId).toList();

        when(orderRepository.findAll()).thenReturn(orders);

        var ordersReceived = orderService.getAll();
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).findAll();
        verify(statusTrackerRecordService, times(1)).getCurrentStatusesOfOrders(orderIds);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(orderRepository.findAll()).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getAll();
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).findAll();
        verify(statusTrackerRecordService, never()).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByUser() {

        var userId = 99L;

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setUserId(userId));

        var orderIds = orders.stream().map(Order::getId).toList();

        when(orderRepository.findAllByUserId(userId)).thenReturn(orders);

        var ordersReceived = orderService.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).findAllByUserId(userId);
        verify(statusTrackerRecordService, times(1)).getCurrentStatusesOfOrders(orderIds);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 0L;

        when(orderRepository.findAllByUserId(anyLong())).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).findAllByUserId(userId);
        verify(statusTrackerRecordService, never()).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByProduct() {

        var productId = 99L;

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setProductId(productId));

        var orderIds = orders.stream().map(Order::getId).toList();

        when(orderRepository.findAllByProductId(productId)).thenReturn(orders);

        var ordersReceived = orderService.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).findAllByProductId(productId);
        verify(statusTrackerRecordService, times(1)).getCurrentStatusesOfOrders(orderIds);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByProduct() {

        var productId = 0L;

        when(orderRepository.findAllByProductId(anyLong())).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).findAllByProductId(productId);
        verify(statusTrackerRecordService, never()).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldCreateAndReturnOrderWhenCreate() {

        var newId = "998";

        when(orderRepository.save(any(Order.class))).thenAnswer(ans -> {
            Order order = ans.getArgument(0);
            order.setId(newId);
            return order;
        });

        when(statusTrackerRecordService.updateStatusForOrder(any(Order.class), any(Status.class))).thenAnswer(ans -> {
            Order order = ans.getArgument(0);
            Status status = ans.getArgument(1);
            order.setStatus(status);
            return order;
        });

        var orderDTO = easyRandom.nextObject(OrderDTO.class);

        var orderCreated = orderService.create(orderDTO);
        assertNotNull(orderCreated);
        assertNotNull(orderCreated.getId());
        assertEquals(Status.CREATED, orderCreated.getStatus());
        assertEquals(newId, orderCreated.getId());
        assertEquals(orderDTO.toOrder(), orderCreated);

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(statusTrackerRecordService, times(1)).updateStatusForOrder(orderCreated, Status.CREATED);
    }

    @Test
    void shouldCreateAndReturnOrderWithFillingCreatedAtFieldWhenCreate() {

        var newId = "998";

        when(orderRepository.save(any(Order.class))).thenAnswer(ans -> {
            Order order = ans.getArgument(0);
            order.setId(newId);
            return order;
        });

        when(statusTrackerRecordService.updateStatusForOrder(any(Order.class), any(Status.class))).thenAnswer(ans -> {
            Order order = ans.getArgument(0);
            Status status = ans.getArgument(1);
            order.setStatus(status);
            return order;
        });

        var orderDTO = easyRandom.nextObject(OrderDTO.class);
        orderDTO.setCreatedAt(null);

        var orderCreated = orderService.create(orderDTO);
        assertNotNull(orderCreated);
        assertNotNull(orderCreated.getId());
        assertNotNull(orderCreated.getCreatedAt());
        assertNotNull(orderCreated.getStatus());
        assertEquals(newId, orderCreated.getId());
        assertEquals(orderDTO.getProductId(), orderCreated.getProductId());
        assertEquals(orderDTO.getUserId(), orderCreated.getUserId());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(statusTrackerRecordService, times(1)).updateStatusForOrder(orderCreated, Status.CREATED);
    }

    @Test
    void shouldUpdateAndReturnOrderWhenUpdate() {

        var orderExisting = easyRandom.nextObject(Order.class);
        var id = "888";

        when(orderRepository.findById(id)).thenReturn(Optional.of(orderExisting));

        when(orderRepository.save(any(Order.class))).thenAnswer(ans -> {
            Order order = ans.getArgument(0);
            order.setId(id);
            return order;
        });

        var orderDTO = easyRandom.nextObject(OrderDTO.class);

        when(statusTrackerRecordService.getCurrentStatusOfOrder(anyString())).thenReturn(Status.IN_PROGRESS);

        var orderUpdated = orderService.update(id, orderDTO);
        assertNotNull(orderUpdated);
        assertNotNull(orderUpdated.getId());
        assertEquals(Status.IN_PROGRESS, orderUpdated.getStatus());
        assertEquals(id, orderUpdated.getId());
        assertEquals(orderDTO.toOrder(), orderUpdated);

        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(statusTrackerRecordService, times(1)).getCurrentStatusOfOrder(id);
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = "888";

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        var orderDTO = easyRandom.nextObject(OrderDTO.class);

        var orderUpdated = orderService.update(id, orderDTO);
        assertNull(orderUpdated);

        verify(orderRepository, times(1)).findById(id);
        verify(orderRepository, never()).save(any(Order.class));
        verify(statusTrackerRecordService, never()).getCurrentStatusOfOrder(anyString());
    }

    @Test
    void shouldReturnOrderWhenChangeOrderStatus() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        var status = Status.DELIVERED;

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        when(statusTrackerRecordService.updateStatusForOrder(any(Order.class), any(Status.class))).thenAnswer(ans -> {
            Order orderParam = ans.getArgument(0);
            Status statusParam = ans.getArgument(1);
            orderParam.setStatus(statusParam);
            return orderParam;
        });

        var orderReceived = orderService.changeOrderStatus(id, status);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);

        verify(orderRepository, times(1)).findById(id);
        verify(statusTrackerRecordService, times(1)).updateStatusForOrder(order, status);
    }

    @Test
    void shouldReturnNullWhenChangeOrderStatus() {

        var id = "999";

        var status = Status.DELIVERED;

        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        when(statusTrackerRecordService.updateStatusForOrder(any(Order.class), any(Status.class))).thenAnswer(ans -> {
            Order orderParam = ans.getArgument(0);
            Status statusParam = ans.getArgument(1);
            orderParam.setStatus(statusParam);
            return orderParam;
        });

        var orderReceived = orderService.changeOrderStatus(id, status);
        assertNull(orderReceived);

        verify(orderRepository, times(1)).findById(id);
        verify(statusTrackerRecordService, never()).updateStatusForOrder(any(Order.class), any(Status.class));
    }

    @Test
    void shouldDeleteWhenDeleteById() {

        var id = "777";

        var statusAfterDeleting = Status.DELETED;

        when(statusTrackerRecordService.updateStatusForOrder(any(Order.class), any(Status.class))).thenAnswer(ans -> {
            Order order = ans.getArgument(0);
            order.setStatus(statusAfterDeleting);
            return order;
        });

        orderService.deleteById(id);

        verify(orderRepository, times(1)).deleteById(id);
        verify(statusTrackerRecordService, times(1)).updateStatusForOrder(id, statusAfterDeleting);
    }
}