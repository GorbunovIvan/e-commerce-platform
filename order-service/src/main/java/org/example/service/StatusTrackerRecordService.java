package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Order;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;
import org.example.model.dto.StatusTrackerRecordDTO;
import org.example.repository.StatusTrackerRecordRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusTrackerRecordService {

    private final StatusTrackerRecordRepository statusTrackerRecordRepository;

    public StatusTrackerRecord getById(String id) {
        log.info("Searching for status-record by id={}", id);
        return statusTrackerRecordRepository.findById(id)
                .orElse(null);
    }

    public List<StatusTrackerRecord> getAllByOrder(String orderId) {
        log.info("Searching for status by orderId={}", orderId);
        return statusTrackerRecordRepository.findAllByOrderId(orderId);
    }

    public List<StatusTrackerRecord> getAllCurrentStatuses() {
        log.info("Searching for all current statuses");
        return statusTrackerRecordRepository.findAllCurrentStatuses();
    }

    public List<StatusTrackerRecord> getAllByCurrentStatus(Status status) {
        log.info("Searching for all current status which are '{}'", status);
        var trackerRecords = getAllCurrentStatuses();
        return trackerRecords.stream()
                .filter(record -> record.getStatusObj().equals(status))
                .toList();
    }

    public Status getCurrentStatusOfOrder(String orderId) {
        log.info("Searching for current status of order={}", orderId);
        return statusTrackerRecordRepository.findFirstByOrderIdOrderByTimeDesc(orderId)
                .map(StatusTrackerRecord::getStatusObj)
                .orElse(null);
    }

    public Map<String, Status> getCurrentStatusesOfOrders(@NonNull List<String> orderIds) {

        log.info("Searching for current statuses of orders={}", orderIds);

        var trackerRecords = statusTrackerRecordRepository.findAllByOrderIdIn(orderIds);
        var mapOrderIdAndLastStatusRecord = trackerRecords.stream()
                .collect(Collectors.groupingBy(StatusTrackerRecord::getOrderId,
                         Collectors.maxBy(Comparator.comparing(StatusTrackerRecord::getTime))));

        var mapResult = new HashMap<String, Status>();

        for (var entry : mapOrderIdAndLastStatusRecord.entrySet()) {

            var orderId = entry.getKey();
            Status status = null;

            var statusRecordOptional = entry.getValue();
            if (statusRecordOptional.isPresent()) {
                status = statusRecordOptional.get().getStatusObj();
            }

            mapResult.put(orderId, status);
        }

        return mapResult;
    }

    public StatusTrackerRecord create(@NonNull StatusTrackerRecordDTO statusTrackerRecordDTO) {

        log.info("Creating new status tracker record '{}'", statusTrackerRecordDTO);

        var statusTrackerRecord = statusTrackerRecordDTO.toStatusTrackerRecord();
        if (statusTrackerRecord.getTime() == null) {
            statusTrackerRecord.setTime(LocalDateTime.now());
        }

        return statusTrackerRecordRepository.save(statusTrackerRecord);
    }

    public Order updateStatusForOrder(@NonNull Order order, @NonNull Status status) {
        var statusReturned = updateStatusForOrder(order.getId(), status);
        order.setStatus(statusReturned);
        return order;
    }

    public Status updateStatusForOrder(String orderId, @NonNull Status status) {

        log.info("Updating status for order with id={}, new status={}", orderId, status);

        var statusTrackerRecordDTO = new StatusTrackerRecordDTO();
        statusTrackerRecordDTO.setOrderId(orderId);
        statusTrackerRecordDTO.setStatus(status);
        statusTrackerRecordDTO.setTime(LocalDateTime.now());

        var statusTrackerRecord = create(statusTrackerRecordDTO);

        return statusTrackerRecord.getStatusObj();
    }

    public void deleteById(String id) {
        log.warn("Deleting status-record by id={}", id);
        statusTrackerRecordRepository.deleteById(id);
    }
}
