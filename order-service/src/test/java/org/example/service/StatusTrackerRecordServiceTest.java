package org.example.service;

import org.example.model.Order;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;
import org.example.model.dto.StatusTrackerRecordDTO;
import org.example.repository.StatusTrackerRecordRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class StatusTrackerRecordServiceTest {

    @Autowired
    private StatusTrackerRecordService statusTrackerRecordService;

    @MockBean
    private StatusTrackerRecordRepository repository;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    void shouldReturnStatusRecordWhenGetById() {

        var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
        var id = statusRecord.getId();

        when(repository.findById(id)).thenReturn(Optional.of(statusRecord));

        var statusRecordReceived = statusTrackerRecordService.getById(id);
        assertNotNull(statusRecordReceived);
        assertEquals(statusRecord, statusRecordReceived);

        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "99";

        when(repository.findById(id)).thenReturn(Optional.empty());

        var statusRecordReceived = statusTrackerRecordService.getById(id);
        assertNull(statusRecordReceived);

        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByOrder() {

        var orderId = "99";

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();

        when(repository.findAllByOrderId(anyString())).thenReturn(statusRecords);

        var statusRecordReceived = statusTrackerRecordService.getAllByOrder(orderId);
        assertNotNull(statusRecordReceived);
        assertEquals(statusRecords, statusRecordReceived);

        verify(repository, times(1)).findAllByOrderId(orderId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByOrder() {

        var orderId = "99";

        when(repository.findAllByOrderId(anyString())).thenReturn(Collections.emptyList());

        var statusRecordReceived = statusTrackerRecordService.getAllByOrder(orderId);
        assertNotNull(statusRecordReceived);
        assertTrue(statusRecordReceived.isEmpty());

        verify(repository, times(1)).findAllByOrderId(orderId);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllCurrentStatuses() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();

        when(repository.findAllCurrentStatuses()).thenReturn(statusRecords);

        var statusRecordsReceived = statusTrackerRecordService.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertEquals(statusRecords, statusRecordsReceived);

        verify(repository, times(1)).findAllCurrentStatuses();
        verify(repository, only()).findAllCurrentStatuses();
    }

    @Test
    void shouldReturnEmptyListWhenGetAllCurrentStatuses() {

        when(repository.findAllCurrentStatuses()).thenReturn(Collections.emptyList());

        var statusRecordsReceived = statusTrackerRecordService.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(repository, times(1)).findAllCurrentStatuses();
        verify(repository, only()).findAllCurrentStatuses();
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByCurrentStatus() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(sr -> sr.setStatus(easyRandom.nextObject(Status.class).name()));

        var statuses = statusRecords.stream().map(StatusTrackerRecord::getStatusObj).distinct().toList();
        
        when(repository.findAllCurrentStatuses()).thenReturn(statusRecords);

        for (var status : statuses) {
            var statusRecordsExpected = statusRecords.stream().filter(statusRecord -> statusRecord.getStatusObj().equals(status)).toList();
            var statusRecordsReceived = statusTrackerRecordService.getAllByCurrentStatus(status);
            assertNotNull(statusRecordsReceived);
            assertEquals(new HashSet<>(statusRecordsExpected), new HashSet<>(statusRecordsReceived));
        }

        verify(repository, times(statuses.size())).findAllCurrentStatuses();
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        when(repository.findAllCurrentStatuses()).thenReturn(Collections.emptyList());

        var statusRecordsReceived = statusTrackerRecordService.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(repository, times(1)).findAllCurrentStatuses();
    }

    @Test
    void shouldReturnStatusWhenGetCurrentStatusOfOrder() {

        var orderId = "111";
        var status = Status.IN_PROGRESS;

        var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
        statusRecord.setOrderId(orderId);
        statusRecord.setStatus(status.name());

        when(repository.findFirstByOrderIdOrderByTimeDesc(orderId)).thenReturn(Optional.of(statusRecord));

        var statusReceived = statusTrackerRecordService.getCurrentStatusOfOrder(orderId);
        assertNotNull(statusReceived);
        assertEquals(status, statusReceived);

        verify(repository, times(1)).findFirstByOrderIdOrderByTimeDesc(orderId);
    }

    @Test
    void shouldReturnNullWhenGetCurrentStatusOfOrder() {

        var orderId = "111";

        when(repository.findFirstByOrderIdOrderByTimeDesc(orderId)).thenReturn(Optional.empty());

        var statusReceived = statusTrackerRecordService.getCurrentStatusOfOrder(orderId);
        assertNull(statusReceived);

        verify(repository, times(1)).findFirstByOrderIdOrderByTimeDesc(orderId);
    }

    @Test
    void shouldReturnMapWhenGetCurrentStatusesOfOrders() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(sr -> sr.setStatus(easyRandom.nextObject(Status.class).name()));

        var ordersId = statusRecords.stream().map(StatusTrackerRecord::getOrderId).distinct().toList();

        var mapExpected = statusRecords.stream()
                .collect(Collectors.toMap(StatusTrackerRecord::getOrderId,
                                            StatusTrackerRecord::getStatusObj));

        when(repository.findAllByOrderIdIn(ordersId)).thenReturn(statusRecords);

        var mapOrderIdAndStatusReceived = statusTrackerRecordService.getCurrentStatusesOfOrders(ordersId);
        assertNotNull(mapOrderIdAndStatusReceived);
        assertEquals(mapExpected, mapOrderIdAndStatusReceived);

        verify(repository, times(1)).findAllByOrderIdIn(ordersId);
    }

    @Test
    void shouldReturnEmptyMapWhenGetCurrentStatusesOfOrders() {

        List<String> ordersId = Collections.emptyList();

        when(repository.findAllByOrderIdIn(ordersId)).thenReturn(Collections.emptyList());

        var mapOrderIdAndStatusReceived = statusTrackerRecordService.getCurrentStatusesOfOrders(ordersId);
        assertNotNull(mapOrderIdAndStatusReceived);
        assertTrue(mapOrderIdAndStatusReceived.isEmpty());

        verify(repository, times(1)).findAllByOrderIdIn(ordersId);
    }

    @Test
    void shouldCreateAndReturnStatusRecordWhenCreate() {

        var newId = "999";

        when(repository.save(any(StatusTrackerRecord.class))).thenAnswer(ans -> {
            var statusRecord = ans.getArgument(0, StatusTrackerRecord.class);
            statusRecord.setId(newId);
            return statusRecord;
        });

        var statusRecordDTO = easyRandom.nextObject(StatusTrackerRecordDTO.class);

        var statusRecordCreated = statusTrackerRecordService.create(statusRecordDTO);
        assertNotNull(statusRecordCreated);
        assertEquals(newId, statusRecordCreated.getId());
        assertEquals(statusRecordDTO.toStatusTrackerRecord(), statusRecordCreated);

        verify(repository, times(1)).save(any(StatusTrackerRecord.class));
    }

    @Test
    void shouldCreateAndReturnStatusRecordWithFillingTimeFieldWhenCreate() {

        var newId = "999";

        when(repository.save(any(StatusTrackerRecord.class))).thenAnswer(ans -> {
            var statusRecord = ans.getArgument(0, StatusTrackerRecord.class);
            statusRecord.setId(newId);
            return statusRecord;
        });

        var statusRecordDTO = easyRandom.nextObject(StatusTrackerRecordDTO.class);
        statusRecordDTO.setTime(null);

        var statusRecordCreated = statusTrackerRecordService.create(statusRecordDTO);
        assertNotNull(statusRecordCreated);
        assertNotNull(statusRecordCreated.getTime());
        assertEquals(newId, statusRecordCreated.getId());
        assertEquals(statusRecordDTO.getOrderId(), statusRecordCreated.getOrderId());
        assertEquals(statusRecordDTO.getStatus(), statusRecordCreated.getStatusObj());

        verify(repository, times(1)).save(any(StatusTrackerRecord.class));
    }

    @Test
    void shouldReturnStatusRecordWhenUpdateStatusForOrder() {

        var order = easyRandom.nextObject(Order.class);
        var status = Status.DELIVERED;

        when(repository.save(any(StatusTrackerRecord.class))).thenAnswer(ans -> ans.getArgument(0, StatusTrackerRecord.class));

        var orderReceived = statusTrackerRecordService.updateStatusForOrder(order, status);
        assertNotNull(orderReceived);
        assertEquals(order, orderReceived);
        assertEquals(status, orderReceived.getStatus());

        verify(repository, times(1)).save(any(StatusTrackerRecord.class));
    }

    @Test
    void shouldDeleteWhenDeleteById() {
        var id = "987";
        statusTrackerRecordService.deleteById(id);
        verify(repository, times(1)).deleteById(id);
        verify(repository, only()).deleteById(id);
    }
}