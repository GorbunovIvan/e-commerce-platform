package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Order;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;
import org.example.model.dto.StatusTrackerRecordDTO;
import org.example.service.StatusTrackerRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/statuses")
@RequiredArgsConstructor
public class StatusTrackerRecordController {

    private final StatusTrackerRecordService statusTrackerRecordService;

    @GetMapping("/{id}")
    public ResponseEntity<StatusTrackerRecord> getById(@PathVariable String id) {
        var record = statusTrackerRecordService.getById(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<StatusTrackerRecord>> getAllByOrder(@PathVariable String orderId) {
        var records = statusTrackerRecordService.getAllByOrder(orderId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/current")
    public ResponseEntity<List<StatusTrackerRecord>> getAllCurrentStatuses() {
        var records = statusTrackerRecordService.getAllCurrentStatuses();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/current/{status}")
    public ResponseEntity<List<StatusTrackerRecord>> getAllByCurrentStatus(@PathVariable Status status) {
        var records = statusTrackerRecordService.getAllByCurrentStatus(status);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/current/order/{orderId}/status")
    public ResponseEntity<Status> getCurrentStatusOfOrder(@PathVariable String orderId) {
        var status = statusTrackerRecordService.getCurrentStatusOfOrder(orderId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    @PostMapping("/current/orders/statuses")
    public ResponseEntity<Map<String, Status>> getCurrentStatusesOfOrders(@RequestBody List<String> orderIds) {
        var records = statusTrackerRecordService.getCurrentStatusesOfOrders(orderIds);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<StatusTrackerRecord> create(@RequestBody StatusTrackerRecordDTO statusTrackerRecordDTO) {
        var record = statusTrackerRecordService.create(statusTrackerRecordDTO);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

    @PostMapping("/update-status/{status}")
    public ResponseEntity<Order> updateStatusForOrder(@RequestBody Order order, @PathVariable("status") Status status) {
        var record = statusTrackerRecordService.updateStatusForOrder(order, status);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable String id) {
        statusTrackerRecordService.deleteById(id);
    }
}
