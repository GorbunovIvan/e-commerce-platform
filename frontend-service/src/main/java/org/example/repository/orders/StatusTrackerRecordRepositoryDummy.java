package org.example.repository.orders;

import lombok.extern.slf4j.Slf4j;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatusTrackerRecordRepositoryDummy implements StatusTrackerRecordRepository {

    private final List<StatusTrackerRecord> statusTrackerRecords = new ArrayList<>();

    @Override
    public StatusTrackerRecord getById(String id) {
        log.info("Searching for status-record by id={}", id);
        return statusTrackerRecords.stream()
                .filter(order -> Objects.equals(order.getId(), id))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<StatusTrackerRecord> getByIds(Set<String> ids) {
        log.info("Searching for status-records by ids={}", ids);
        return statusTrackerRecords.stream()
                .filter(order -> ids.contains(order.getId()))
                .toList();
    }

    @Override
    public List<StatusTrackerRecord> getAllByOrder(String orderId) {
        log.info("Searching for status by orderId={}", orderId);
        return statusTrackerRecords.stream()
                .filter(order -> order.getOrder() != null)
                .filter(order -> Objects.equals(order.getOrder().getId(), orderId))
                .sorted()
                .toList();
    }

    @Override
    public List<StatusTrackerRecord> getAllCurrentStatuses() {

        log.info("Searching for all current statuses");

        var ordersAndTheirLastStatusRecords = statusTrackerRecords.stream()
                .collect(Collectors.groupingBy(StatusTrackerRecord::getOrder,
                                    Collectors.maxBy(Comparator.comparing(StatusTrackerRecord::getTime))));

        return ordersAndTheirLastStatusRecords.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<StatusTrackerRecord> getAllByCurrentStatus(Status status) {
        log.info("Searching for all current status which are '{}'", status);
        var currentStatusesRecords = getAllCurrentStatuses();
        return currentStatusesRecords.stream()
                .filter(record -> Objects.equals(record.getStatus(), status))
                .toList();
    }

    @Override
    public Status getCurrentStatusOfOrder(String orderId) {
        log.info("Searching for current status of order={}", orderId);
        return statusTrackerRecords.stream()
                .filter(statusRecord -> statusRecord.getOrder() != null)
                .filter(statusRecord -> Objects.equals(statusRecord.getOrder().getId(), orderId))
                .max(Comparator.comparing(StatusTrackerRecord::getTime))
                .map(StatusTrackerRecord::getStatus)
                .orElse(null);
    }

    protected Map<Order, Status> getCurrentStatusesOfOrders(List<String> orderIds) {
        log.info("Searching for current statuses of orders={}", orderIds);
        var orderIdsSet = new HashSet<>(orderIds);
        return statusTrackerRecords.stream()
                .filter(statusRecord -> statusRecord.getOrder() != null)
                .filter(statusRecord -> orderIdsSet.contains(statusRecord.getOrder().getId()))
                .collect(Collectors.groupingBy(
                            StatusTrackerRecord::getOrder,
                            Collectors.collectingAndThen(
                                    Collectors.maxBy(Comparator.comparing(StatusTrackerRecord::getTime)),
                                    statusRecordOpt -> statusRecordOpt.map(StatusTrackerRecord::getStatus).orElse(null)
                            )
                        ));
    }

    protected synchronized StatusTrackerRecord create(StatusTrackerRecord statusTrackerRecord) {
        log.info("Creating new status tracker record '{}'", statusTrackerRecord);
        var nextId = nextId();
        statusTrackerRecord.setId(nextId);
        if (statusTrackerRecord.getTime() == null) {
            statusTrackerRecord.setTime(LocalDateTime.now());
        }
        statusTrackerRecords.add(statusTrackerRecord);
        return statusTrackerRecord;
    }

    protected Order updateStatusForOrder(Order order, Status status) {

        log.info("Updating status for order with id={}, new status={}", order.getId(), status);

        var statusTrackerRecord = new StatusTrackerRecord();
        statusTrackerRecord.setOrder(order);
        statusTrackerRecord.setStatus(status);
        statusTrackerRecord.setTime(LocalDateTime.now());

        statusTrackerRecord = create(statusTrackerRecord);

        order.setStatus(statusTrackerRecord.getStatus());

        return statusTrackerRecord.getOrder();
    }

    protected void deleteById(String id) {
        log.warn("Deleting status-record by id={}", id);
        var indexOfStatusRecordInList = getIndexOfStatusRecordInListById(id);
        if (indexOfStatusRecordInList == -1) {
            return;
        }
        statusTrackerRecords.remove(indexOfStatusRecordInList);
    }

    private String nextId() {

        var ids = statusTrackerRecords.stream()
                .map(StatusTrackerRecord::getId)
                .collect(Collectors.toSet());

        while (true) {
            var nextId = String.valueOf(new Random().nextLong());
            if (!ids.contains(nextId)) {
                return nextId;
            }
        }
    }

    private int getIndexOfStatusRecordInListById(String id) {
        for (int i = 0; i < statusTrackerRecords.size(); i++) {
            if (statusTrackerRecords.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
