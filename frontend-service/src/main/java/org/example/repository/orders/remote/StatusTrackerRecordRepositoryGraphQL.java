package org.example.repository.orders.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.example.model.orders.dto.StatusTrackerRecordDTO;
import org.example.repository.orders.StatusTrackerRecordRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@Primary
@ConditionalOnProperty(name = "order-service.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(name = "httpGraphQlClient")
@RequiredArgsConstructor
@Slf4j
public class StatusTrackerRecordRepositoryGraphQL implements StatusTrackerRecordRepository {

    private final HttpGraphQlClient httpGraphQlClient;

    @Override
    public StatusTrackerRecord getById(String id) {

        log.info("Searching for status-record by id={}", id);

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

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getStatusRecordById")
                .toEntity(StatusTrackerRecordDTO.class)
                .toFuture();

        try {
            var statusRecordDTO = responseFuture.get();
            if (statusRecordDTO == null) {
                return null;
            }
            return statusRecordDTO.toStatusTrackerRecord();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get status-record by id - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<StatusTrackerRecord> getByIds(Set<String> ids) {

        log.info("Searching for status-records by ids={}", ids);

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

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getStatusRecordsByIds")
                .toEntityList(StatusTrackerRecordDTO.class)
                .toFuture();

        try {
            var statusRecordsDTO = responseFuture.get();
            return StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get status-records by ids - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<StatusTrackerRecord> getAllByOrder(String orderId) {

        log.info("Searching for statuses by orderId={}", orderId);

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

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getAllStatusRecordsByOrder")
                .toEntityList(StatusTrackerRecordDTO.class)
                .toFuture();

        try {
            var statusRecordsDTO = responseFuture.get();
            return StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get status-records by orderId - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<StatusTrackerRecord> getAllCurrentStatuses() {

        log.info("Searching for all current statuses");

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

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getAllCurrentStatusRecords")
                .toEntityList(StatusTrackerRecordDTO.class)
                .toFuture();

        try {
            var statusRecordsDTO = responseFuture.get();
            return StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get all status-records - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<StatusTrackerRecord> getAllByCurrentStatus(Status status) {

        log.info("Searching for all current status which are '{}'", status);

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

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getAllStatusRecordsByCurrentStatus")
                .toEntityList(StatusTrackerRecordDTO.class)
                .toFuture();

        try {
            var statusRecordsDTO = responseFuture.get();
            return StatusTrackerRecordDTO.toStatusTrackerRecords(statusRecordsDTO);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get all currents status-records by status - {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Status getCurrentStatusOfOrder(String orderId) {

        log.info("Searching for current status of order={}", orderId);

        String query = """
                {
                  getCurrentStatusOfOrder(orderId: "%s")
                }
                """;

        query = String.format(query, orderId);

        var responseFuture = httpGraphQlClient.document(query)
                .retrieve("getCurrentStatusOfOrder")
                .toEntity(Status.class)
                .toFuture();

        try {
            return responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get current status by orderId - {}", e.getMessage());
            return null;
        }
    }
}
