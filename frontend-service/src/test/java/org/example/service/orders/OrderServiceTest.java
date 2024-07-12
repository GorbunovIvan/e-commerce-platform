package org.example.service.orders;

import org.example.exception.NotFoundException;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.example.repository.orders.OrderRepository;
import org.example.service.modelsBinding.ModelBinder;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private ModelBinder modelBinder;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        when(modelBinder.bindFields(any())).thenAnswer(ans -> ans.getArgument(0));
    }

    @Test
    void shouldReturnOrderWhenGetById() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        when(orderRepository.getById(id)).thenReturn(order);

        var orderReceived = orderService.getById(id);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);

        verify(orderRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(order);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "999";

        when(orderRepository.getById(anyString())).thenReturn(null);

        var orderReceived = orderService.getById(id);
        assertNull(orderReceived);

        verify(orderRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAll() {

        var orders = easyRandom.objects(Order.class, 5).toList();

        when(orderRepository.getAll()).thenReturn(orders);

        var ordersReceived = orderService.getAll();
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).getAll();
        verify(modelBinder, times(1)).bindFields(orders);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(orderRepository.getAll()).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getAll();
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).getAll();
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setUser(user));

        when(orderRepository.getAllByUser(user.getId())).thenReturn(orders);

        var ordersReceived = orderService.getAllByUser(user);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).getAllByUser(user.getId());
        verify(modelBinder, times(1)).bindFields(orders);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 0L;

        when(orderRepository.getAllByUser(anyLong())).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).getAllByUser(userId);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByProduct() {

        var product = easyRandom.nextObject(Product.class);

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setProduct(product));

        when(orderRepository.getAllByProduct(product.getId())).thenReturn(orders);

        var ordersReceived = orderService.getAllByProduct(product);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(orders, ordersReceived);

        verify(orderRepository, times(1)).getAllByProduct(product.getId());
        verify(modelBinder, times(1)).bindFields(orders);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByProduct() {

        var productId = 0L;

        when(orderRepository.getAllByProduct(anyLong())).thenReturn(Collections.emptyList());

        var ordersReceived = orderService.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(orderRepository, times(1)).getAllByProduct(productId);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldCreateAndReturnNewProductWhenCreate() {

        var order = easyRandom.nextObject(Order.class);

        when(orderRepository.create(order)).thenReturn(order);

        var orderResult = orderService.create(order);
        assertNotNull(orderResult);
        assertEquals(order, orderResult);

        verify(orderRepository, times(1)).create(order);
        verify(modelBinder, times(1)).bindFields(order);
    }

    @Test
    void shouldUpdateAndReturnOrderWhenUpdate() {

        var orderExisting = easyRandom.nextObject(Order.class);
        var id = orderExisting.getId();

        when(orderRepository.update(id, orderExisting)).thenReturn(orderExisting);

        var orderUpdated = orderService.update(id, orderExisting);
        assertNotNull(orderUpdated);
        assertEquals(id, orderUpdated.getId());
        assertEquals(orderExisting, orderUpdated);

        verify(orderRepository, times(1)).update(id, orderExisting);
        verify(modelBinder, times(1)).bindFields(orderExisting);
    }

    @Test
    void shouldAsynchronouslyUpdateAndReturnOrderWhenUpdate() {

        var orderExisting = easyRandom.nextObject(Order.class);
        var id = orderExisting.getId();

        when(orderRepository.update(id, orderExisting)).thenReturn(null);
        when(orderRepository.getById(id)).thenReturn(orderExisting);

        var orderUpdated = orderService.update(id, orderExisting);
        assertNotNull(orderUpdated);
        assertEquals(id, orderUpdated.getId());
        assertEquals(orderExisting, orderUpdated);

        verify(orderRepository, times(1)).update(id, orderExisting);
        verify(orderRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(orderExisting);
    }

    @Test
    void shouldReturnNullWhenUpdate() {

        var id = "1";

        var order = easyRandom.nextObject(Order.class);

        when(orderRepository.update(id, order)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> orderService.update(id, order));

        verify(orderRepository, times(1)).update(id, order);
    }

    @Test
    void shouldReturnNullWhenChangeOrderStatus() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();
        var status = Status.DELIVERED;

        when(orderRepository.changeOrderStatus(id, status)).thenReturn(order);

        var orderResult = orderService.changeOrderStatus(id, status);
        assertNotNull(orderResult);
        assertEquals(order, orderResult);

        verify(orderRepository, times(1)).changeOrderStatus(id, status);
        verify(modelBinder, times(1)).bindFields(order);
    }

    @Test
    void shouldDeleteOrderWhenDelete() {
        var id = "98";
        orderService.deleteById(id);
        verify(orderRepository, times(1)).deleteById(id);
    }
}