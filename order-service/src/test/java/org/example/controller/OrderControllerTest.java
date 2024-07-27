package org.example.controller;

import org.example.model.Order;
import org.example.service.OrderService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    private HttpGraphQlTester graphQlTester;

    @MockBean
    private OrderService orderService;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp(@Autowired ApplicationContext applicationContext) {

        if (graphQlTester == null) {
            var webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                    .configureClient()
                    .baseUrl("/graphql")
                    .build();
            graphQlTester = HttpGraphQlTester.create(webTestClient);
        }
    }

    @Test
    void shouldReturnOrderWhenGetOrderById() {

        var order = easyRandom.nextObject(Order.class);
        var id = order.getId();

        when(orderService.getById(id)).thenReturn(order);

        String query = """
                {
                  getOrderById(id: "%s") {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        query = String.format(query, id);

        graphQlTester.document(query)
                .execute()
                .path("data.getOrderById")
                .hasValue()
                .entity(Order.class)
                .isEqualTo(order);

        verify(orderService, times(1)).getById(id);
    }

    @Test
    void shouldReturnNullWhenGetOrderById() {

        var id = "999";

        when(orderService.getById(id)).thenReturn(null);

        String query = """
                {
                  getOrderById(id: "%s") {
                      id
                  }
                }
                """;

        query = String.format(query, id);

        graphQlTester.document(query)
                .execute()
                .path("data.getOrderById")
                .valueIsNull();

        verify(orderService, times(1)).getById(id);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetByIds() {

        var orders = easyRandom.objects(Order.class, 5).toList();
        var ids = orders.stream().map(Order::getId).collect(Collectors.toCollection(LinkedHashSet::new));

        when(orderService.getByIds(ids)).thenReturn(orders);

        String query = """
                {
                  getOrdersByIds(ids: ["%s"]) {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        var idsAsParam = String.join("\", \"", ids);
        query = String.format(query, idsAsParam);

        var ordersReceived = graphQlTester.document(query)
                .execute()
                .path("data.getOrdersByIds")
                .hasValue()
                .entityList(Order.class)
                .hasSizeGreaterThan(0)
                .get();

        assertEquals(orders, ordersReceived);

        verify(orderService, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnEmptyListOfOrdersWhenGetByIds() {

        var ids = easyRandom.objects(String.class, 3).collect(Collectors.toCollection(LinkedHashSet::new));

        when(orderService.getByIds(ids)).thenReturn(Collections.emptyList());

        String query = """
                {
                  getOrdersByIds(ids: ["%s"]) {
                      id
                  }
                }
                """;

        var idsAsParam = String.join("\", \"", ids);
        query = String.format(query, idsAsParam);

        graphQlTester.document(query)
                .execute()
                .path("data.getOrdersByIds")
                .hasValue()
                .entityList(Order.class)
                .hasSize(0);

        verify(orderService, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllOrders() {

        var orders = easyRandom.objects(Order.class, 5).toList();

        when(orderService.getAll()).thenReturn(orders);

        String query = """
                {
                  getAllOrders {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        var ordersReceived = graphQlTester.document(query)
                .execute()
                .path("data.getAllOrders")
                .hasValue()
                .entityList(Order.class)
                .get();

        assertEquals(orders, ordersReceived);

        verify(orderService, times(1)).getAll();
    }

    @Test
    void shouldReturnEmptyListOfOrdersWhenGetAllOrders() {

        when(orderService.getAll()).thenReturn(Collections.emptyList());

        String query = """
                {
                  getAllOrders {
                      id
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("data.getAllOrders")
                .hasValue()
                .entityList(Order.class)
                .hasSize(0);

        verify(orderService, times(1)).getAll();
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllOrdersByUser() {

        var userId = 99L;

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setUserId(userId));

        when(orderService.getAllByUser(userId)).thenReturn(orders);

        String query = """
                {
                  getAllOrdersByUser(userId: "%s") {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        query = String.format(query, userId);

        var ordersReceived = graphQlTester.document(query)
                .execute()
                .path("data.getAllOrdersByUser")
                .hasValue()
                .entityList(Order.class)
                .get();

        assertEquals(orders, ordersReceived);

        verify(orderService, times(1)).getAllByUser(userId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllOrdersByUser() {

        var userId = 99L;

        when(orderService.getAllByUser(userId)).thenReturn(Collections.emptyList());

        String query = """
                {
                  getAllOrdersByUser(userId: "%s") {
                      id
                  }
                }
                """;

        query = String.format(query, userId);

        graphQlTester.document(query)
                .execute()
                .path("data.getAllOrdersByUser")
                .hasValue()
                .entityList(Order.class)
                .hasSize(0);

        verify(orderService, times(1)).getAllByUser(userId);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllOrdersByProduct() {

        var productId = 88L;

        var orders = easyRandom.objects(Order.class, 5).toList();
        orders.forEach(order -> order.setProductId(productId));

        when(orderService.getAllByProduct(productId)).thenReturn(orders);

        String query = """
                {
                  getAllOrdersByProduct(productId: "%s") {
                      id
                      userId
                      productId
                      createdAt
                      status
                  }
                }
                """;

        query = String.format(query, productId);

        var ordersReceived = graphQlTester.document(query)
                .execute()
                .path("data.getAllOrdersByProduct")
                .hasValue()
                .entityList(Order.class)
                .get();

        assertEquals(orders, ordersReceived);

        verify(orderService, times(1)).getAllByProduct(productId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllOrdersByProduct() {

        var productId = 88L;

        when(orderService.getAllByProduct(productId)).thenReturn(Collections.emptyList());

        String query = """
                {
                  getAllOrdersByProduct(productId: "%s") {
                      id
                  }
                }
                """;

        query = String.format(query, productId);

        graphQlTester.document(query)
                .execute()
                .path("data.getAllOrdersByProduct")
                .hasValue()
                .entityList(Order.class)
                .hasSize(0);

        verify(orderService, times(1)).getAllByProduct(productId);
    }
}