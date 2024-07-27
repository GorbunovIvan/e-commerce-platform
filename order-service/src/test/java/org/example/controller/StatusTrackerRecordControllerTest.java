package org.example.controller;

import org.example.model.Status;
import org.example.model.StatusTrackerRecord;
import org.example.service.StatusTrackerRecordService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatusTrackerRecordControllerTest {

    private HttpGraphQlTester graphQlTester;

    @MockBean
    private StatusTrackerRecordService statusTrackerRecordService;

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
    void shouldReturnStatusRecordWhenGetStatusRecordById() {

        var statusRecord = easyRandom.nextObject(StatusTrackerRecord.class);
        var id = statusRecord.getId();

        when(statusTrackerRecordService.getById(id)).thenReturn(statusRecord);

        String query = """
                {
                  getStatusRecordById(id: "%s") {
                     id
                     orderId
                     status
                     time
                  }
                }
                """;

        query = String.format(query, id);

        graphQlTester.document(query)
                .execute()
                .path("data.getStatusRecordById")
                .hasValue()
                .entity(StatusTrackerRecord.class)
                .isEqualTo(statusRecord);

        verify(statusTrackerRecordService, times(1)).getById(id);
    }

    @Test
    void shouldReturnNullWhenGetStatusRecordById() {

        var id = "99";

        String query = """
                {
                  getStatusRecordById(id: "%s") {
                     id
                  }
                }
                """;

        query = String.format(query, id);

        graphQlTester.document(query)
                .execute()
                .path("data.getStatusRecordById")
                .valueIsNull();

        verify(statusTrackerRecordService, times(1)).getById(id);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetStatusRecordsByIds() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        var ids = statusRecords.stream().map(StatusTrackerRecord::getId).collect(Collectors.toCollection(LinkedHashSet::new));

        when(statusTrackerRecordService.getByIds(ids)).thenReturn(statusRecords);

        String query = """
                {
                  getStatusRecordsByIds(ids: ["%s"]) {
                     id
                     orderId
                     status
                     time
                  }
                }
                """;

        var idsAsParam = String.join("\", \"", ids);
        query = String.format(query, idsAsParam);

        var statusRecordsReceived = graphQlTester.document(query)
                .execute()
                .path("data.getStatusRecordsByIds")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .hasSizeGreaterThan(0)
                .get();

        assertNotNull(statusRecordsReceived);
        assertEquals(statusRecords, statusRecordsReceived);

        verify(statusTrackerRecordService, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnEmptyListWhenGetStatusRecordsByIds() {

        var ids = easyRandom.objects(String.class, 3).collect(Collectors.toCollection(LinkedHashSet::new));

        when(statusTrackerRecordService.getByIds(ids)).thenReturn(Collections.emptyList());

        String query = """
                {
                  getStatusRecordsByIds(ids: ["%s"]) {
                     id
                  }
                }
                """;

        var idsAsParam = String.join("\", \"", ids);
        query = String.format(query, idsAsParam);

        graphQlTester.document(query)
                .execute()
                .path("data.getStatusRecordsByIds")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .hasSize(0);

        verify(statusTrackerRecordService, times(1)).getByIds(ids);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllStatusRecordsByOrder() {

        var orderId = "99";

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(sr -> sr.setOrderId(orderId));
        statusRecords.forEach(sr -> sr.setStatus(easyRandom.nextObject(Status.class).name()));

        when(statusTrackerRecordService.getAllByOrder(orderId)).thenReturn(statusRecords);

        String query = """
                {
                  getAllStatusRecordsByOrder(orderId: "%s") {
                     id
                     orderId
                     status
                     time
                  }
                }
                """;

        query = String.format(query, orderId);

        var statusRecordsReceived = graphQlTester.document(query)
                .execute()
                .path("data.getAllStatusRecordsByOrder")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .get();

        assertNotNull(statusRecordsReceived);
        assertEquals(statusRecords, statusRecordsReceived);

        verify(statusTrackerRecordService, times(1)).getAllByOrder(orderId);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllStatusRecordsByOrder() {

        var orderId = "99";

        when(statusTrackerRecordService.getAllByOrder(orderId)).thenReturn(Collections.emptyList());

        String query = """
                {
                  getAllStatusRecordsByOrder(orderId: "%s") {
                     id
                  }
                }
                """;

        query = String.format(query, orderId);

        graphQlTester.document(query)
                .execute()
                .path("data.getAllStatusRecordsByOrder")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .hasSize(0);

        verify(statusTrackerRecordService, times(1)).getAllByOrder(orderId);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllCurrentStatuses() {

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();

        when(statusTrackerRecordService.getAllCurrentStatuses()).thenReturn(statusRecords);

        String query = """
                {
                  getAllCurrentStatusRecords {
                     id
                     orderId
                     status
                     time
                  }
                }
                """;

        var statusRecordsReceived = graphQlTester.document(query)
                .execute()
                .path("data.getAllCurrentStatusRecords")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .get();

        assertNotNull(statusRecordsReceived);
        assertEquals(statusRecords, statusRecordsReceived);

        verify(statusTrackerRecordService, times(1)).getAllCurrentStatuses();
        verify(statusTrackerRecordService, only()).getAllCurrentStatuses();
    }

    @Test
    void shouldReturnEmptyListWhenGetAllCurrentStatuses() {

        when(statusTrackerRecordService.getAllCurrentStatuses()).thenReturn(Collections.emptyList());

        String query = """
                {
                  getAllCurrentStatusRecords {
                     id
                  }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("data.getAllCurrentStatusRecords")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .hasSize(0);

        verify(statusTrackerRecordService, times(1)).getAllCurrentStatuses();
        verify(statusTrackerRecordService, only()).getAllCurrentStatuses();
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllStatusRecordsByCurrentStatus() {

        var status = Status.DELIVERED;

        var statusRecords = easyRandom.objects(StatusTrackerRecord.class, 5).toList();
        statusRecords.forEach(sr -> sr.setStatus(status.name()));

        when(statusTrackerRecordService.getAllByCurrentStatus(status)).thenReturn(statusRecords);

        String query = """
                {
                  getAllStatusRecordsByCurrentStatus(status: "%s") {
                     id
                     orderId
                     status
                     time
                  }
                }
                """;

        query = String.format(query, status.name());

        var statusRecordsReceived = graphQlTester.document(query)
                .execute()
                .path("data.getAllStatusRecordsByCurrentStatus")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .get();

        assertEquals(statusRecords, statusRecordsReceived);

        verify(statusTrackerRecordService, times(1)).getAllByCurrentStatus(status);
        verify(statusTrackerRecordService, times(1)).getAllByCurrentStatus(any(Status.class));
    }

    @Test
    void shouldReturnEmptyListWhenGetAllStatusRecordsByCurrentStatus() {

        String query = """
                {
                  getAllStatusRecordsByCurrentStatus(status: "%s") {
                     id
                  }
                }
                """;

        query = String.format(query, "null");

        graphQlTester.document(query)
                .execute()
                .path("data.getAllStatusRecordsByCurrentStatus")
                .hasValue()
                .entityList(StatusTrackerRecord.class)
                .hasSize(0);

        verify(statusTrackerRecordService, never()).getAllByCurrentStatus(any());
    }

    @Test
    void shouldReturnStatusWhenGetCurrentStatusOfOrder() {

        var orderId = "111";
        var status = Status.IN_PROGRESS;

        when(statusTrackerRecordService.getCurrentStatusOfOrder(orderId)).thenReturn(status);

        String query = """
                {
                  getCurrentStatusOfOrder(orderId: "%s")
                }
                """;

        query = String.format(query, orderId);

        graphQlTester.document(query)
                .execute()
                .path("data.getCurrentStatusOfOrder")
                .hasValue()
                .entity(String.class)
                .isEqualTo(status.name());

        verify(statusTrackerRecordService, times(1)).getCurrentStatusOfOrder(orderId);
    }

    @Test
    void shouldReturnNullWhenGetCurrentStatusOfOrder() {

        var orderId = "111";

        when(statusTrackerRecordService.getCurrentStatusOfOrder(orderId)).thenReturn(null);

        String query = """
                {
                  getCurrentStatusOfOrder(orderId: "%s")
                }
                """;

        query = String.format(query, orderId);

        graphQlTester.document(query)
                .execute()
                .path("data.getCurrentStatusOfOrder")
                .valueIsNull();

        verify(statusTrackerRecordService, times(1)).getCurrentStatusOfOrder(orderId);
    }
}