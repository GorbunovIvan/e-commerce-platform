package org.example.repository.orders.remote;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "order-service.enabled=true")
class OrderRepositoryFacadeTest {

    @Autowired
    private OrderRepositoryFacade orderRepositoryFacade;

    @MockBean
    private OrderRepositoryGraphQL orderRepositoryGraphQL;
    @MockBean
    private OrderRepositoryRabbitMQPublisher orderRepositoryRabbitMQ;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnOrderWhenGetById() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        when(orderRepositoryGraphQL.getById(id)).thenReturn(order);

        var orderReceived = orderRepositoryFacade.getById(id);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);

        verify(orderRepositoryGraphQL, times(1)).getById(id);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetByIds() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        var ids = orders.stream().map(Order::getId).collect(Collectors.toSet());

        when(orderRepositoryGraphQL.getByIds(ids)).thenReturn(orders);

        var ordersReceived = orderRepositoryFacade.getByIds(ids);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepositoryGraphQL, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAll() {

        var orders = easyRandom.objects(Order.class, 5).toList();

        when(orderRepositoryGraphQL.getAll()).thenReturn(orders);

        var ordersReceived = orderRepositoryFacade.getAll();
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepositoryGraphQL, times(1)).getAll();
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);
        var userId = user.getId();

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setUser(user));

        when(orderRepositoryGraphQL.getAllByUser(userId)).thenReturn(orders);

        var ordersReceived = orderRepositoryFacade.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepositoryGraphQL, times(1)).getAllByUser(userId);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByProduct() {

        var product = easyRandom.nextObject(Product.class);
        var productId = product.getId();

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setProduct(product));

        when(orderRepositoryGraphQL.getAllByProduct(productId)).thenReturn(orders);

        var ordersReceived = orderRepositoryFacade.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepositoryGraphQL, times(1)).getAllByProduct(productId);
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() {

        var order = easyRandom.nextObject(Order.class);

        when(orderRepositoryRabbitMQ.create(order)).thenReturn(order);

        var orderResult = orderRepositoryFacade.create(order);
        assertNotNull(orderResult);
        assertEquals(order, orderResult);

        verify(orderRepositoryRabbitMQ, times(1)).create(order);
    }

    @Test
    void shouldUpdateAndReturnOrderWhenUpdate() {

        var orderExisting = easyRandom.nextObject(Order.class);
        var id = orderExisting.getId();

        when(orderRepositoryRabbitMQ.update(id, orderExisting)).thenReturn(orderExisting);

        var orderUpdated = orderRepositoryFacade.update(id, orderExisting);
        assertNotNull(orderUpdated);
        assertEquals(id, orderUpdated.getId());
        assertEquals(orderExisting, orderUpdated);

        verify(orderRepositoryRabbitMQ, times(1)).update(id, orderExisting);
    }

    @Test
    void shouldReturnOrderWhenChangeOrderStatus() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();
        var status = Status.DELIVERED;

        when(orderRepositoryRabbitMQ.changeOrderStatus(id, status)).thenReturn(order);

        var orderResult = orderRepositoryFacade.changeOrderStatus(id, status);
        assertNotNull(orderResult);
        assertEquals(order, orderResult);

        verify(orderRepositoryRabbitMQ, times(1)).changeOrderStatus(id, status);
    }

    @Test
    void shouldDeleteOrderWhenDelete() {
        var id = "98";
        orderRepositoryFacade.deleteById(id);
        verify(orderRepositoryRabbitMQ, times(1)).deleteById(id);
    }    
}