package org.example.service.orders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.example.repository.orders.StatusTrackerRecordRepository;
import org.example.service.modelsBinding.ModelBinder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusTrackerRecordService {

    private final StatusTrackerRecordRepository statusTrackerRecordRepository;
    private final ModelBinder modelBinder;

    public StatusTrackerRecord getById(String id) {
        log.info("Searching for status-record by id={}", id);
        var result = statusTrackerRecordRepository.getById(id);
        return modelBinder.bindFields(result);
    }

    public List<StatusTrackerRecord> getAllByOrder(Order order) {
        if (order == null) {
            return Collections.emptyList();
        }
        return this.getAllByOrder(order.getId());
    }

    public List<StatusTrackerRecord> getAllByOrder(String orderId) {
        log.info("Searching for status by orderId={}", orderId);
        var result = statusTrackerRecordRepository.getAllByOrder(orderId);
        return modelBinder.bindFields(result);
    }

    public List<StatusTrackerRecord> getAllCurrentStatuses() {
        log.info("Searching for all current statuses");
        var result = statusTrackerRecordRepository.getAllCurrentStatuses();
        return modelBinder.bindFields(result);
    }

    public List<StatusTrackerRecord> getAllByCurrentStatus(Status status) {
        log.info("Searching for all current status which are '{}'", status);
        var result = statusTrackerRecordRepository.getAllByCurrentStatus(status);
        return modelBinder.bindFields(result);
    }

    public Status getCurrentStatusOfOrder(Order order) {
        if (order == null) {
            return null;
        }
        return this.getCurrentStatusOfOrder(order.getId());
    }

    public Status getCurrentStatusOfOrder(String orderId) {
        log.info("Searching for current status of order={}", orderId);
        var result = statusTrackerRecordRepository.getCurrentStatusOfOrder(orderId);
        return modelBinder.bindFields(result);
    }
}
