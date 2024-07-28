package org.example.repository.orders;

import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface StatusTrackerRecordRepository {
    StatusTrackerRecord getById(String id);
    List<StatusTrackerRecord> getByIds(Set<String> ids);
    List<StatusTrackerRecord> getAllByOrder(String orderId);
    List<StatusTrackerRecord> getAllCurrentStatuses();
    List<StatusTrackerRecord> getAllByCurrentStatus(Status status);
    Status getCurrentStatusOfOrder(String orderId);
}
