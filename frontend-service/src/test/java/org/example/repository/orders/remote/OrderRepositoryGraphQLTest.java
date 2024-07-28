package org.example.repository.orders.remote;

import org.example.model.orders.dto.OrderDTO;
import org.example.model.products.Product;
import org.example.model.users.User;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "order-service.enabled=true")
class OrderRepositoryGraphQLTest {

    @Autowired
    private OrderRepositoryGraphQL orderRepositoryGraphQL;

    @SpyBean
    private HttpGraphQlClient httpGraphQlClient;
    @MockBean
    private GraphQlClient.RequestSpec requestSpec;
    @MockBean
    private GraphQlClient.RetrieveSpec retrieveSpec;
    @MockBean
    private ClientGraphQlResponse graphQlResponse;
    @MockBean
    private ClientResponseField clientResponseField;

    // The next beans are mocked just to ignore them
    @MockBean
    private OrderRepositoryFacade orderRepositoryFacade;
    @MockBean
    private OrderRepositoryRabbitMQPublisher orderRepositoryRabbitMQPublisher;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        when(httpGraphQlClient.document(anyString())).thenReturn(requestSpec);
        when(requestSpec.retrieve(anyString())).thenReturn(retrieveSpec);
        when(requestSpec.execute()).thenReturn(Mono.just(graphQlResponse));
        when(graphQlResponse.field(anyString())).thenReturn(clientResponseField);
    }

    @Test
    void shouldReturnOrderWhenGetById() {

        var orderDTO = easyRandom.nextObject(OrderDTO.class);
        var id = orderDTO.getId();

        when(retrieveSpec.toEntity(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(orderDTO));

        var orderReceived = orderRepositoryGraphQL.getById(id);
        assertNotNull(orderReceived);
        assertEquals(orderDTO.toOrder(), orderReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getOrderById");
        verify(retrieveSpec, times(1)).toEntity(OrderDTO.class);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "999";

        when(retrieveSpec.toEntity(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.empty());

        var orderReceived = orderRepositoryGraphQL.getById(id);
        assertNull(orderReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getOrderById");
        verify(retrieveSpec, times(1)).toEntity(OrderDTO.class);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetByIds() {

        var ordersDTO = easyRandom.objects(OrderDTO.class, 5).toList();

        var ids = ordersDTO.stream().map(OrderDTO::getId).collect(Collectors.toSet());

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(ordersDTO));

        var ordersReceived = orderRepositoryGraphQL.getByIds(ids);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(OrderDTO.toOrders(ordersDTO), ordersReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getOrdersByIds");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var ids = easyRandom.objects(String.class, 4).collect(Collectors.toSet());

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var ordersReceived = orderRepositoryGraphQL.getByIds(ids);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getOrdersByIds");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAll() {

        var ordersDTO = easyRandom.objects(OrderDTO.class, 5).toList();

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(ordersDTO));

        var ordersReceived = orderRepositoryGraphQL.getAll();
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(OrderDTO.toOrders(ordersDTO), ordersReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllOrders");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetAll() {

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var ordersReceived = orderRepositoryGraphQL.getAll();
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllOrders");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByUser() {

        var user = easyRandom.nextObject(User.class);
        var userId = user.getId();

        var ordersDTO = easyRandom.objects(OrderDTO.class, 5).toList();
        ordersDTO.forEach(order -> order.setUserId(userId));

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(ordersDTO));

        var ordersReceived = orderRepositoryGraphQL.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(OrderDTO.toOrders(ordersDTO), ordersReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllOrdersByUser");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByUser() {

        var userId = 0L;

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var ordersReceived = orderRepositoryGraphQL.getAllByUser(userId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllOrdersByUser");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnListOfOrdersWhenGetAllByProduct() {

        var product = easyRandom.nextObject(Product.class);
        var productId = product.getId();

        var ordersDTO = easyRandom.objects(OrderDTO.class, 5).toList();
        ordersDTO.forEach(order -> order.setProductId(productId));

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(ordersDTO));

        var ordersReceived = orderRepositoryGraphQL.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(OrderDTO.toOrders(ordersDTO), ordersReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllOrdersByProduct");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByProduct() {

        var productId = 0L;

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<OrderDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var ordersReceived = orderRepositoryGraphQL.getAllByProduct(productId);
        assertNotNull(ordersReceived);
        assertTrue(ordersReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllOrdersByProduct");
        verify(retrieveSpec, times(1)).toEntityList(OrderDTO.class);
    }
}