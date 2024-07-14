package org.example.service.orders;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.example.repository.orders.StatusTrackerRecordRepositoryDummy;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class StatusTrackerRecordServiceTest {

    @Autowired
    private StatusTrackerRecordService statusTrackerRecordService;

    @MockBean
    private StatusTrackerRecordRepositoryDummy statusTrackerRecordRepository;
    @MockBean
    private ModelBinder modelBinder;

    private final EasyRandom easyRandom = new EasyRandom();

    @BeforeEach
    void setUp() {
        when(modelBinder.bindFields(any())).thenAnswer(ans -> ans.getArgument(0));
    }

    @Test
    void shouldReturnStatusRecordWhenGetById() {

        var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
        var id = statusRecord.getId();

        when(statusTrackerRecordRepository.getById(id)).thenReturn(statusRecord);

        var statusRecordReceived = statusTrackerRecordService.getById(id);
        assertNotNull(statusRecordReceived);
        assertEquals(statusRecord, statusRecordReceived);

        verify(statusTrackerRecordRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(statusRecord);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "99";

        when(statusTrackerRecordRepository.getById(id)).thenReturn(null);

        var statusRecordReceived = statusTrackerRecordService.getById(id);
        assertNull(statusRecordReceived);

        verify(statusTrackerRecordRepository, times(1)).getById(id);
        verify(modelBinder, times(1)).bindFields(null);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByOrder() {

        var order = easyRandom.nextObject(Order.class);

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(statusRecord -> statusRecord.setOrder(order));

        when(statusTrackerRecordRepository.getAllByOrder(order.getId())).thenReturn(statusRecords);

        var statusRecordReceived = statusTrackerRecordService.getAllByOrder(order);
        assertNotNull(statusRecordReceived);
        assertEquals(statusRecords, statusRecordReceived);

        verify(statusTrackerRecordRepository, times(1)).getAllByOrder(order.getId());
        verify(modelBinder, times(1)).bindFields(statusRecords);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByOrder() {

        var orderId = "99";

        when(statusTrackerRecordRepository.getAllByOrder(anyString())).thenReturn(Collections.emptyList());

        var statusRecordReceived = statusTrackerRecordService.getAllByOrder(orderId);
        assertNotNull(statusRecordReceived);
        assertTrue(statusRecordReceived.isEmpty());

        verify(statusTrackerRecordRepository, times(1)).getAllByOrder(orderId);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllCurrentStatuses() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();

        when(statusTrackerRecordRepository.getAllCurrentStatuses()).thenReturn(statusRecords);

        var statusRecordsReceived = statusTrackerRecordService.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertEquals(statusRecords, statusRecordsReceived);

        verify(statusTrackerRecordRepository, times(1)).getAllCurrentStatuses();
        verify(statusTrackerRecordRepository, only()).getAllCurrentStatuses();
        verify(modelBinder, times(1)).bindFields(statusRecords);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllCurrentStatuses() {

        when(statusTrackerRecordRepository.getAllCurrentStatuses()).thenReturn(Collections.emptyList());

        var statusRecordsReceived = statusTrackerRecordService.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(statusTrackerRecordRepository, times(1)).getAllCurrentStatuses();
        verify(statusTrackerRecordRepository, only()).getAllCurrentStatuses();
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(record -> record.setStatus(status));

        when(statusTrackerRecordRepository.getAllByCurrentStatus(status)).thenReturn(statusRecords);

        var statusRecordsReceived = statusTrackerRecordService.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertEquals(statusRecords, statusRecordsReceived);

        verify(statusTrackerRecordRepository, times(1)).getAllByCurrentStatus(status);
        verify(modelBinder, times(1)).bindFields(statusRecords);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        when(statusTrackerRecordRepository.getAllCurrentStatuses()).thenReturn(Collections.emptyList());

        var statusRecordsReceived = statusTrackerRecordService.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(statusTrackerRecordRepository, times(1)).getAllByCurrentStatus(status);
        verify(modelBinder, times(1)).bindFields(Collections.emptyList());
    }

    @Test
    void shouldReturnStatusWhenGetCurrentStatusOfOrder() {

        var order = easyRandom.nextObject(Order.class);
        var status = Status.IN_PROGRESS;

        when(statusTrackerRecordRepository.getCurrentStatusOfOrder(order.getId())).thenReturn(status);

        var statusReceived = statusTrackerRecordService.getCurrentStatusOfOrder(order);
        assertNotNull(statusReceived);
        assertEquals(status, statusReceived);

        verify(statusTrackerRecordRepository, times(1)).getCurrentStatusOfOrder(order.getId());
        verify(modelBinder, times(1)).bindFields(status);
    }

    @Test
    void shouldReturnNullWhenGetCurrentStatusOfOrder() {

        var order = easyRandom.nextObject(Order.class);

        when(statusTrackerRecordRepository.getCurrentStatusOfOrder(order.getId())).thenReturn(null);

        var statusReceived = statusTrackerRecordService.getCurrentStatusOfOrder(order);
        assertNull(statusReceived);

        verify(statusTrackerRecordRepository, times(1)).getCurrentStatusOfOrder(order.getId());
        verify(modelBinder, times(1)).bindFields(null);
    }
}