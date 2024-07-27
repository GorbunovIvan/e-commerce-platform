package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;
import org.example.service.StatusTrackerRecordService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StatusTrackerRecordController {

    private final StatusTrackerRecordService statusTrackerRecordService;

    @QueryMapping
    public StatusTrackerRecord getStatusRecordById(@Argument String id) {
        return statusTrackerRecordService.getById(id);
    }

    @QueryMapping
    public List<StatusTrackerRecord> getStatusRecordsByIds(@Argument Collection<String> ids) {
        return statusTrackerRecordService.getByIds(ids);
    }

    @QueryMapping
    public List<StatusTrackerRecord> getAllStatusRecordsByOrder(@Argument String orderId) {
        return statusTrackerRecordService.getAllByOrder(orderId);
    }

    @QueryMapping
    public List<StatusTrackerRecord> getAllCurrentStatusRecords() {
        return statusTrackerRecordService.getAllCurrentStatuses();
    }

    @QueryMapping
    public List<StatusTrackerRecord> getAllStatusRecordsByCurrentStatus(@Argument String status) {
        var statusObj = Status.statusByName(status);
        if (statusObj == null) {
            log.error("Incorrect status was passed: {}", status);
            return Collections.emptyList();
        }
        return statusTrackerRecordService.getAllByCurrentStatus(statusObj);
    }

    @QueryMapping
    public String getCurrentStatusOfOrder(@Argument String orderId) {
        var status = statusTrackerRecordService.getCurrentStatusOfOrder(orderId);
        if (status == null) {
            log.warn("No current status of order {} was found", orderId);
            return null;
        }
        return status.name();
    }
}
