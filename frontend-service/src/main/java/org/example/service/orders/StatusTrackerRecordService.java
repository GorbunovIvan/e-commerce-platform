package org.example.service.orders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.example.repository.orders.StatusTrackerRecordRepositoryDummy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusTrackerRecordService {

    private final StatusTrackerRecordRepositoryDummy statusTrackerRecordRepository;

    public StatusTrackerRecord getById(String id) {
        log.info("Searching for status-record by id={}", id);
        return statusTrackerRecordRepository.getById(id);
    }

    public List<StatusTrackerRecord> getAllByOrder(Order order) {
        if (order == null) {
            return Collections.emptyList();
        }
        return this.getAllByOrder(order.getId());
    }

    public List<StatusTrackerRecord> getAllByOrder(String orderId) {
        log.info("Searching for status by orderId={}", orderId);
        return statusTrackerRecordRepository.getAllByOrder(orderId);
    }

    public List<StatusTrackerRecord> getAllCurrentStatuses() {
        log.info("Searching for all current statuses");
        return statusTrackerRecordRepository.getAllCurrentStatuses();
    }

    public List<StatusTrackerRecord> getAllByCurrentStatus(Status status) {
        log.info("Searching for all current status which are '{}'", status);
        return statusTrackerRecordRepository.getAllByCurrentStatus(status);
    }

    public Status getCurrentStatusOfOrder(Order order) {
        if (order == null) {
            return null;
        }
        return this.getCurrentStatusOfOrder(order.getId());
    }

    public Status getCurrentStatusOfOrder(String orderId) {
        log.info("Searching for current status of order={}", orderId);
        return statusTrackerRecordRepository.getCurrentStatusOfOrder(orderId);
    }
}
