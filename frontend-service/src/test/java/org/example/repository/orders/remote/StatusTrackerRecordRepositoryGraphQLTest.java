package org.example.repository.orders.remote;

import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.dto.StatusTrackerRecordDTO;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "order-service.enabled=true")
class StatusTrackerRecordRepositoryGraphQLTest {

    @Autowired
    private StatusTrackerRecordRepositoryGraphQL statusTrackerRecordRepositoryGraphQL;

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
    void shouldReturnStatusRecordWhenGetById() {

        var statusRecordDTO = easyRandom.nextObject(StatusTrackerRecordDTO.class);

        var id = statusRecordDTO.getId();

        when(retrieveSpec.toEntity(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(statusRecordDTO));

        var statusRecordDTOReceived = statusTrackerRecordRepositoryGraphQL.getById(id);
        assertNotNull(statusRecordDTOReceived);
        assertEquals(statusRecordDTO.toStatusTrackerRecord(), statusRecordDTOReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getStatusRecordById");
        verify(retrieveSpec, times(1)).toEntity(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnNullWhenGetById() {

        var id = "99";

        when(retrieveSpec.toEntity(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.empty());

        var statusRecordDTOReceived = statusTrackerRecordRepositoryGraphQL.getById(id);
        assertNull(statusRecordDTOReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getStatusRecordById");
        verify(retrieveSpec, times(1)).toEntity(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetByIds() {

        var statusRecordsDTO = easyRandom.objects(StatusTrackerRecordDTO.class, 5).toList();
        var ids = statusRecordsDTO.stream().map(StatusTrackerRecordDTO::getId).collect(Collectors.toSet());

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(statusRecordsDTO));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getByIds(ids);
        assertNotNull(statusRecordsReceived);
        assertEquals(StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO), statusRecordsReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getStatusRecordsByIds");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetByIds() {

        var ids = easyRandom.objects(String.class, 5).collect(Collectors.toSet());

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getByIds(ids);
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getStatusRecordsByIds");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByOrder() {

        var orderId = easyRandom.nextObject(String.class);

        var statusRecordsDTO = easyRandom.objects(StatusTrackerRecordDTO.class, 5).toList();
        statusRecordsDTO.forEach(statusRecordDTO -> statusRecordDTO.setOrderId(orderId));

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(statusRecordsDTO));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getAllByOrder(orderId);
        assertNotNull(statusRecordsReceived);
        assertEquals(StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO), statusRecordsReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllStatusRecordsByOrder");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByOrder() {

        var orderId = "99";

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getAllByOrder(orderId);
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllStatusRecordsByOrder");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllCurrentStatuses() {

        var statusRecordsDTO = easyRandom.objects(StatusTrackerRecordDTO.class, 5).toList();

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(statusRecordsDTO));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertEquals(StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO), statusRecordsReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllCurrentStatusRecords");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllCurrentStatuses() {

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getAllCurrentStatuses();
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllCurrentStatusRecords");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnListOfStatusRecordsWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        var statusRecordsDTO = easyRandom.objects(StatusTrackerRecordDTO.class, 5).toList();
        statusRecordsDTO.forEach(record -> record.setStatus(status));

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(statusRecordsDTO));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertEquals(StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO), statusRecordsReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllStatusRecordsByCurrentStatus");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnEmptyListWhenGetAllByCurrentStatus() {

        var status = Status.IN_PROGRESS;

        when(retrieveSpec.toEntityList(ArgumentMatchers.<Class<StatusTrackerRecordDTO>>any())).thenReturn(Mono.just(Collections.emptyList()));

        var statusRecordsReceived = statusTrackerRecordRepositoryGraphQL.getAllByCurrentStatus(status);
        assertNotNull(statusRecordsReceived);
        assertTrue(statusRecordsReceived.isEmpty());

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getAllStatusRecordsByCurrentStatus");
        verify(retrieveSpec, times(1)).toEntityList(StatusTrackerRecordDTO.class);
    }

    @Test
    void shouldReturnStatusWhenGetCurrentStatusOfOrder() {

        var order = easyRandom.nextObject(Order.class);
        var status = Status.IN_PROGRESS;

        when(retrieveSpec.toEntity(ArgumentMatchers.<Class<Status>>any())).thenReturn(Mono.just(status));

        var statusReceived = statusTrackerRecordRepositoryGraphQL.getCurrentStatusOfOrder(order.getId());
        assertNotNull(statusReceived);
        assertEquals(status, statusReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getCurrentStatusOfOrder");
        verify(retrieveSpec, times(1)).toEntity(Status.class);
    }

    @Test
    void shouldReturnNullWhenGetCurrentStatusOfOrder() {

        var orderId = "88";

        when(retrieveSpec.toEntity(ArgumentMatchers.<Class<Status>>any())).thenReturn(Mono.empty());

        var statusReceived = statusTrackerRecordRepositoryGraphQL.getCurrentStatusOfOrder(orderId);
        assertNull(statusReceived);

        verify(httpGraphQlClient, times(1)).document(anyString());
        verify(requestSpec, times(1)).retrieve("getCurrentStatusOfOrder");
        verify(retrieveSpec, times(1)).toEntity(Status.class);
    }
}