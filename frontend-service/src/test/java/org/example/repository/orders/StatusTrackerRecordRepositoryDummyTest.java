package org.example.repository.orders;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.example.repository.ReflectUtil;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StatusTrackerRecordRepositoryDummyTest {

    @Autowired
    private StatusTrackerRecordRepositoryDummy statusTrackerRecordRepositoryDummy;

    @Autowired
    private ReflectUtil reflectUtil;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>());
    }

    @Test
    void shouldReturnStatusRecordWhenGetById() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var statusRecord = statusRecords.getFirst();
        var id = statusRecord.getId();

        var statusRecordReceived = statusTrackerRecordRepositoryDummy.getById(id);
        assertNotNull(statusRecordReceived);
        assertEquals(statusRecord, statusRecordReceived);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var id = "99";

        var statusRecordReceived = statusTrackerRecordRepositoryDummy.getById(id);
        assertNull(statusRecordReceived);
    }

    @Test
    void shouldReturnStatusRecordsWhenGetByIds() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var ids = statusRecords.stream().map(StatusTrackerRecord::getId).collect(Collectors.toSet());

        var statusRecordReceived = statusTrackerRecordRepositoryDummy.getByIds(ids);
        assertNotNull(statusRecordReceived);
        assertFalse(statusRecordReceived.isEmpty());
        assertEquals(statusRecords, statusRecordReceived);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var ids = easyRandom.objects(String.class, 3).collect(Collectors.toSet());

        var statusRecordReceived = statusTrackerRecordRepositoryDummy.getByIds(ids);
        assertNotNull(statusRecordReceived);
        assertTrue(statusRecordReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByOrder() {

        var order = easyRandom.nextObject(Order.class);

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(statusRecord -> statusRecord.setOrder(order));
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var statusRecordReceived = statusTrackerRecordRepositoryDummy.getAllByOrder(order.getId());
        assertNotNull(statusRecordReceived);
        assertEquals(new HashSet<>(statusRecords), new HashSet<>(statusRecordReceived));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByOrder() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var orderId = "99";

        var statusRecordReceived = statusTrackerRecordRepositoryDummy.getAllByOrder(orderId);
        assertNotNull(statusRecordReceived);
        assertTrue(statusRecordReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllCurrentStatuses() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var statusRecordsReceived = statusTrackerRecordRepositoryDummy.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertEquals(new HashSet<>(statusRecords), new HashSet<>(statusRecordsReceived));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllCurrentStatuses() {
        var statusRecordsReceived = statusTrackerRecordRepositoryDummy.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(record -> record.setStatus(status));
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var statusRecordsReceived = statusTrackerRecordRepositoryDummy.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertEquals(new HashSet<>(statusRecords), new HashSet<>(statusRecordsReceived));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        for (var record : statusRecords) {
            if (Objects.equals(record.getStatus(), status)) {
                record.setStatus(Status.CREATED);
            }
        }
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var statusRecordsReceived = statusTrackerRecordRepositoryDummy.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());
    }

    @Test
    void shouldReturnStatusWhenGetCurrentStatusOfOrder() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var statusRecord = statusRecords.getFirst();

        var order = statusRecords.getFirst().getOrder();
        var status = statusRecord.getStatus();

        var statusReceived = statusTrackerRecordRepositoryDummy.getCurrentStatusOfOrder(order.getId());
        assertNotNull(statusReceived);
        assertEquals(status, statusReceived);
    }

    @Test
    void shouldReturnNullWhenGetCurrentStatusOfOrder() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var orderId = "55";

        var statusReceived = statusTrackerRecordRepositoryDummy.getCurrentStatusOfOrder(orderId);
        assertNull(statusReceived);
    }

    @Test
    void shouldReturnMapWhenGetCurrentStatusesOfOrders() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var orders = statusRecords.stream().map(StatusTrackerRecord::getOrder).collect(Collectors.toSet());
        var statuses = statusRecords.stream().map(StatusTrackerRecord::getStatus).collect(Collectors.toSet());

        var orderIds = orders.stream().map(Order::getId).toList();

        var ordersWithStatusesReceived = statusTrackerRecordRepositoryDummy.getCurrentStatusesOfOrders(orderIds);
        assertNotNull(ordersWithStatusesReceived);
        assertEquals(orders, ordersWithStatusesReceived.keySet());
        assertEquals(statuses, new HashSet<>(ordersWithStatusesReceived.values()));
    }

    @Test
    void shouldReturnEmptyMapWhenGetCurrentStatusesOfOrders() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecords));

        var orderIds = easyRandom.objects(String.class, 3).toList();

        var ordersWithStatusesReceived = statusTrackerRecordRepositoryDummy.getCurrentStatusesOfOrders(orderIds);
        assertNotNull(ordersWithStatusesReceived);
        assertTrue(ordersWithStatusesReceived.isEmpty());
    }

    @Test
    void shouldCreateAndReturnNewStatusTrackerRecordWhenCreate() {

        var statusRecordsExisting = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecordsExisting));

        var statusTrackerRecordExpected = easyRandom.nextObject(StatusTrackerRecord.class);

        var statusTrackerRecord = statusTrackerRecordRepositoryDummy.create(statusTrackerRecordExpected);
        assertNotNull(statusTrackerRecord);
        assertEquals(statusTrackerRecordExpected, statusTrackerRecord);

        var statusTrackerRecordsAfterOperation = getStatusTrackerRecordsFromStatusTrackerRecordRepositoryDummy();
        assertTrue(statusTrackerRecordsAfterOperation.contains(statusTrackerRecord));
        assertEquals(statusRecordsExisting.size() + 1, statusTrackerRecordsAfterOperation.size());
    }

    @Test
    void shouldUpdateAndReturnStatusTrackerRecordWhenUpdate() {

        var statusRecordsExisting = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusRecordsExisting));

        var statusRecordExisting = statusRecordsExisting.getFirst();

        var order = statusRecordExisting.getOrder();
        var status = statusRecordExisting.getStatus();

        var orderReturned = statusTrackerRecordRepositoryDummy.updateStatusForOrder(order, status);
        assertNotNull(orderReturned);
        assertEquals(order, orderReturned);
        assertEquals(status, orderReturned.getStatus());

        var statusTrackerRecordsAfterOperation = getStatusTrackerRecordsFromStatusTrackerRecordRepositoryDummy();
        assertEquals(statusRecordsExisting.size() + 1, statusTrackerRecordsAfterOperation.size());
    }

    @Test
    void shouldDeleteStatusTrackerRecordWhenDelete() {

        var statusTrackerRecordsExising = easyRandom.objects(StatusTrackerRecord.class, 3).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusTrackerRecordsExising));

        var statusTrackerRecordToDelete = statusTrackerRecordsExising.getFirst();
        var id = statusTrackerRecordToDelete.getId();

        statusTrackerRecordRepositoryDummy.deleteById(id);

        var statusTrackerRecordsAfterOperation = getStatusTrackerRecordsFromStatusTrackerRecordRepositoryDummy();
        assertFalse(statusTrackerRecordsAfterOperation.contains(statusTrackerRecordToDelete));
        assertEquals(statusTrackerRecordsExising.size() - 1, statusTrackerRecordsAfterOperation.size());
    }

    @Test
    void shouldNotDeleteStatusTrackerRecordWhenDelete() {

        var statusTrackerRecordsExising = easyRandom.objects(StatusTrackerRecord.class, 3).toList();
        setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(new ArrayList<>(statusTrackerRecordsExising));

        var id = "111";

        statusTrackerRecordRepositoryDummy.deleteById(id);

        var statusTrackerRecordsAfterOperation = getStatusTrackerRecordsFromStatusTrackerRecordRepositoryDummy();
        assertEquals(statusTrackerRecordsExising.size(), statusTrackerRecordsAfterOperation.size());
    }

    private List<StatusTrackerRecord> getStatusTrackerRecordsFromStatusTrackerRecordRepositoryDummy() {
        return reflectUtil.getValueOfObjectField(statusTrackerRecordRepositoryDummy, "statusTrackerRecords", Collections::emptyList);
    }

    private void setStatusTrackerRecordsToStatusTrackerRecordRepositoryDummy(List<StatusTrackerRecord> statusTrackerRecords) {
        reflectUtil.setValueToObjectField(statusTrackerRecordRepositoryDummy, "statusTrackerRecords", statusTrackerRecords);
    }
}