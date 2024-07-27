package org.example.repository;

import org.example.model.Order;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final EasyRandom easyRandom = new EasyRandom();

    private List<Long> productIds;
    private List<Long> userIds;

    private List<Order> orders = new ArrayList<>();

    @BeforeEach
    void setUp() {

        this.productIds = easyRandom.objects(Long.class, 4).toList();
        this.userIds = easyRandom.objects(Long.class, 3).toList();

        for (var productId : productIds) {
            for (var userId : userIds) {
                var order = new Order();
                order.setProductId(productId);
                order.setUserId(userId);
                order.setCreatedAt(easyRandom.nextObject(LocalDateTime.class));
                this.orders.add(order);
            }
        }

        this.orders = mongoTemplate.insertAll(orders).stream().toList();
    }

    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }

    @Test
    void shouldReturnListOfOrdersWhenFindAllByIdIn() {
        var ids = orders.stream().map(Order::getId).toList();
        var ordersReceived = repository.findAllByIdIn(ids);
        assertFalse(ordersReceived.isEmpty());
        assertEquals(new HashSet<>(orders), new HashSet<>(ordersReceived));
    }

    @Test
    void shouldReturnListWithOneOrderWhenFindAllByIdIn() {
        for (var order : orders) {
            var ids = List.of(order.getId());
            var ordersReceived = repository.findAllByIdIn(ids);
            assertEquals(1, ordersReceived.size());
            assertEquals(List.of(order), ordersReceived);
        }
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByIdIn() {
        var ids = new ArrayList<String>();
        var ordersReceived = repository.findAllByIdIn(ids);
        assertTrue(ordersReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfOrdersWhenFindAllByProductId() {
        for (var productId : productIds) {
            var ordersExpected = orders.stream().filter(order -> order.getProductId().equals(productId)).toList();
            var ordersReceived = repository.findAllByProductId(productId);
            assertEquals(new HashSet<>(ordersExpected), new HashSet<>(ordersReceived));
        }
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByProductId() {
        var ordersReceived = repository.findAllByProductId(0L);
        assertTrue(ordersReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfOrdersWhenFindAllByUserId() {
        for (var userId : userIds) {
            var ordersExpected = orders.stream().filter(order -> order.getUserId().equals(userId)).toList();
            var ordersReceived = repository.findAllByUserId(userId);
            assertEquals(new HashSet<>(ordersExpected), new HashSet<>(ordersReceived));
        }
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByUserId() {
        var ordersReceived = repository.findAllByUserId(0L);
        assertTrue(ordersReceived.isEmpty());
    }
}