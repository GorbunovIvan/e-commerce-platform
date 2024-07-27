package org.example.repository;

import org.example.model.Order;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class StatusTrackerRecordRepositoryTest {

    @Autowired
    private StatusTrackerRecordRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final EasyRandom easyRandom = new EasyRandom();

    private List<Order> orders;
    private List<StatusTrackerRecord> statusRecords;

    @BeforeEach
    void setUp() {

        // Generating orders
        var orders = easyRandom.objects(Order.class, 3).toList();
        orders.forEach(order -> order.setId(null));
        orders = mongoTemplate.insertAll(orders).stream().toList();

        // Generating status-records
        List<StatusTrackerRecord> statusRecords = new ArrayList<>();

        for (var order : orders) {

            Status currentStatus = null;

            for (int i = 0; i < 3; i++) {

                currentStatus = Status.nextStatusAfter(currentStatus);
                if (currentStatus == null) {
                    currentStatus = Status.CREATED;
                }

                var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
                statusRecord.setId(null);
                statusRecord.setOrderId(order.getId());
                statusRecord.setStatus(currentStatus.toString());
                statusRecords.add(statusRecord);
            }
        }

        statusRecords = mongoTemplate.insertAll(statusRecords).stream().toList();

        this.orders = orders;
        this.statusRecords = statusRecords;
    }

    @AfterEach
    void tearDown() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            mongoTemplate.dropCollection(collectionName);
        }
    }

    @Test
    void shouldReturnListOfOrdersWhenFindAllByIdIn() {
        var ids = statusRecords.stream().map(StatusTrackerRecord::getId).toList();
        var statusRecordsReceived = repository.findAllByIdIn(ids);
        assertFalse(statusRecordsReceived.isEmpty());
        assertEquals(new HashSet<>(statusRecords), new HashSet<>(statusRecordsReceived));
    }

    @Test
    void shouldReturnListWithOneOrderWhenFindAllByIdIn() {
        for (var statusRecord : statusRecords) {
            var ids = List.of(statusRecord.getId());
            var statusRecordsReceived = repository.findAllByIdIn(ids);
            assertEquals(1, statusRecordsReceived.size());
            assertEquals(List.of(statusRecord), statusRecordsReceived);
        }
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByIdIn() {
        var ids = new ArrayList<String>();
        var statusRecordsReceived = repository.findAllByIdIn(ids);
        assertTrue(statusRecordsReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenFindAllByOrderId() {
        for (var order : orders) {
            var statusRecordsExpected = statusRecords.stream().filter(st -> order.getId().equals(st.getOrderId())).toList();
            var statusRecordsReceived = repository.findAllByOrderId(order.getId());
            assertEquals(new HashSet<>(statusRecordsExpected), new HashSet<>(statusRecordsReceived));
        }
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByOrderId() {
        var statusRecordsReceived = repository.findAllByOrderId("--");
        assertTrue(statusRecordsReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenFindAllByOrderIdIn() {
        var orderIds = orders.stream().map(Order::getId).toList();
        var statusRecordsReceived = repository.findAllByOrderIdIn(orderIds);
        assertEquals(new HashSet<>(statusRecords), new HashSet<>(statusRecordsReceived));
    }

    @Test
    void shouldReturnListOfStatusRecordsBySingleOrderWhenFindAllByOrderIdIn() {
        for (var order : orders) {
            var orderIds = List.of(order.getId());
            var statusRecordsExpected = statusRecords.stream().filter(st -> orderIds.contains(st.getOrderId())).toList();
            var statusRecordsReceived = repository.findAllByOrderIdIn(orderIds);
            assertEquals(new HashSet<>(statusRecordsExpected), new HashSet<>(statusRecordsReceived));
        }
    }

    @Test
    void shouldReturnEmptyListWhenFindAllByOrderIdIn() {
        var statusRecordsReceived = repository.findAllByOrderIdIn(Collections.emptyList());
        assertTrue(statusRecordsReceived.isEmpty());
    }

    @Test
    void shouldReturnCurrentStatusRecordWhenFindFirstByOrderIdOrderByTimeDesc() {
        for (var order : orders) {
            var statusRecordExpected = statusRecords.stream().filter(st -> order.getId().equals(st.getOrderId())).max(Comparator.comparing(StatusTrackerRecord::getTime));
            var statusRecordReceived = repository.findFirstByOrderIdOrderByTimeDesc(order.getId());
            assertEquals(statusRecordExpected, statusRecordReceived);
        }
    }

    @Test
    void shouldReturnEmptyWhenFindFirstByOrderIdOrderByTimeDesc() {
        var statusRecordReceived = repository.findFirstByOrderIdOrderByTimeDesc("--");
        assertTrue(statusRecordReceived.isEmpty());
    }

    @Test
    void shouldReturnCurrentStatusRecordsWhenFindAllCurrentStatuses() {

        var currentStatusRecordsByOrderId = statusRecords.stream()
                .collect(Collectors.groupingBy(StatusTrackerRecord::getOrderId,
                            Collectors.maxBy(Comparator.comparing(StatusTrackerRecord::getTime))));

        var statusRecordsExpected = currentStatusRecordsByOrderId.values().stream().flatMap(Optional::stream).toList();
        var statusRecordsReceived = repository.findAllCurrentStatuses();
        assertEquals(new HashSet<>(statusRecordsExpected), new HashSet<>(statusRecordsReceived));
    }
}