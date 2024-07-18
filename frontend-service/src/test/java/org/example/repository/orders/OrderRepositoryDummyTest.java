package org.example.repository.orders;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.example.repository.ReflectUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderRepositoryDummyTest {

    @Autowired
    private OrderRepositoryDummy orderRepositoryDummy;

    @MockBean
    private StatusTrackerRecordRepositoryDummy statusTrackerRecordRepositoryDummy;

    @Autowired
    private ReflectUtil reflectUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        setOrdersToOrderRepositoryDummy(new ArrayList<>());
    }

    @Test
    void shouldReturnOrderWhenGetById() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var order = orders.getFirst();
        var id = order.getId();

        var orderReceived = orderRepositoryDummy.getById(id);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);

        verify(statusTrackerRecordRepositoryDummy, times(1)).getCurrentStatusOfOrder(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var id = "999";

        var orderReceived = orderRepositoryDummy.getById(id);
        assertNull(orderReceived);

        verify(statusTrackerRecordRepositoryDummy, never()).getCurrentStatusOfOrder(any());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetByIds() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var ids = orders.stream().map(Order::getId).collect(Collectors.toSet());

        var orderReceived = orderRepositoryDummy.getByIds(ids);
        assertNotNull(orderReceived);
        assertEquals(orders, orderReceived);

        verify(statusTrackerRecordRepositoryDummy, times(1)).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var ids = easyRandom.objects(String.class, 3).collect(Collectors.toSet());

        var ordersReceived = orderRepositoryDummy.getByIds(ids);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(statusTrackerRecordRepositoryDummy, never()).getCurrentStatusOfOrder(any());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAll() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var ordersReceived = orderRepositoryDummy.getAll();
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(new HashSet<>(orders), new HashSet<>(ordersReceived));

        verify(statusTrackerRecordRepositoryDummy, times(1)).getCurrentStatusesOfOrders(anyList());
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        var ordersReceived = orderRepositoryDummy.getAll();
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(statusTrackerRecordRepositoryDummy, never()).getCurrentStatusOfOrder(any());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setUser(user));
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var ordersReceived = orderRepositoryDummy.getAllByUser(user.getId());
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(new HashSet<>(orders), new HashSet<>(ordersReceived));

        verify(statusTrackerRecordRepositoryDummy, times(1)).getCurrentStatusesOfOrders(anyList());
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var userId = 0L;

        var ordersReceived = orderRepositoryDummy.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(statusTrackerRecordRepositoryDummy, never()).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByProduct() {

        var product = easyRandom.nextObject(Product.class);

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setProduct(product));
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var ordersReceived = orderRepositoryDummy.getAllByProduct(product.getId());
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(new HashSet<>(orders), new HashSet<>(ordersReceived));

        verify(statusTrackerRecordRepositoryDummy, times(1)).getCurrentStatusesOfOrders(anyList());
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByProduct() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(orders));

        var productId = 0L;

        var ordersReceived = orderRepositoryDummy.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(statusTrackerRecordRepositoryDummy, never()).getCurrentStatusesOfOrders(any());
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var order = easyRandom.nextObject(Order.class);

        var orderResult = orderRepositoryDummy.create(order);
        assertNotNull(orderResult);
        assertEquals(order, orderResult);

        var ordersAfterOperation = getOrdersFromOrderRepositoryDummy();
        assertTrue(ordersAfterOperation.contains(orderResult));
        assertEquals(ordersExisting.size() + 1, ordersAfterOperation.size());

        verify(statusTrackerRecordRepositoryDummy, times(1)).updateStatusForOrder(order, Status.CREATED);
    }

    @Test
    void shouldUpdateAndReturnOrderWhenUpdate() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var orderExisting = ordersExisting.getFirst();
        var id = orderExisting.getId();

        var orderUpdated = orderRepositoryDummy.update(id, orderExisting);
        assertNotNull(orderUpdated);
        assertEquals(id, orderUpdated.getId());
        assertEquals(orderExisting, orderUpdated);

        var ordersAfterOperation = getOrdersFromOrderRepositoryDummy();
        assertTrue(ordersAfterOperation.contains(orderUpdated));
        assertEquals(ordersExisting.size(), ordersAfterOperation.size());

        verify(statusTrackerRecordRepositoryDummy, atLeastOnce()).getCurrentStatusOfOrder(id);
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var id = "1";

        var order = easyRandom.nextObject(Order.class);
        assertNull(orderRepositoryDummy.update(id, order));

        var ordersAfterOperation = getOrdersFromOrderRepositoryDummy();
        assertEquals(ordersExisting.size(), ordersAfterOperation.size());

        verify(statusTrackerRecordRepositoryDummy, never()).getCurrentStatusOfOrder(any());
    }

    @Test
    void shouldReturnOrderWhenChangeOrderStatus() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var order = ordersExisting.getFirst();
        var id = order.getId();
        var status = Status.DELIVERED;

        when(statusTrackerRecordRepositoryDummy.updateStatusForOrder(any(Order.class), any(Status.class)))
                .thenAnswer(ans -> ans.getArgument(0, Order.class));

        var orderResult = orderRepositoryDummy.changeOrderStatus(id, status);
        assertNotNull(orderResult);
        assertEquals(order, orderResult);

        var ordersAfterOperation = getOrdersFromOrderRepositoryDummy();
        assertEquals(ordersExisting.size(), ordersAfterOperation.size());

        verify(statusTrackerRecordRepositoryDummy, times(1)).updateStatusForOrder(order, status);
    }

    @Test
    void shouldReturnNullWhenChangeOrderStatus() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var id = "888";
        var status = Status.DELIVERED;

        var orderResult = orderRepositoryDummy.changeOrderStatus(id, status);
        assertNull(orderResult);

        verify(statusTrackerRecordRepositoryDummy, never()).updateStatusForOrder(any(), any());
    }

    @Test
    void shouldDeleteOrderWhenDelete() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var orderToDelete = ordersExisting.getFirst();
        var id = orderToDelete.getId();

        orderRepositoryDummy.deleteById(id);

        var ordersAfterOperation = getOrdersFromOrderRepositoryDummy();
        assertFalse(ordersAfterOperation.contains(orderToDelete));
        assertEquals(ordersExisting.size() - 1, ordersAfterOperation.size());
    }

    @Test
    void shouldNotDeleteOrderWhenDelete() {

        var ordersExisting = easyRandom.objects(Order.class, 5).toList();
        setOrdersToOrderRepositoryDummy(new ArrayList<>(ordersExisting));

        var id = "97";

        orderRepositoryDummy.deleteById(id);

        var ordersAfterOperation = getOrdersFromOrderRepositoryDummy();
        assertEquals(ordersExisting.size(), ordersAfterOperation.size());
    }
    
    private List<Order> getOrdersFromOrderRepositoryDummy() {
        return reflectUtil.getValueOfObjectField(orderRepositoryDummy, "orders", Collections::emptyList);
    }

    private void setOrdersToOrderRepositoryDummy(List<Order> orders) {
        reflectUtil.setValueToObjectField(orderRepositoryDummy, "orders", orders);
    }
}