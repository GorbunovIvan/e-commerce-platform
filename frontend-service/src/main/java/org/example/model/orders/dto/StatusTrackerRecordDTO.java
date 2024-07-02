package org.example.model.orders.dto;

import lombok.*;
import org.example.model.orders.Order;
import org.example.model.orders.Status;
import org.example.model.orders.StatusTrackerRecord;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class StatusTrackerRecordDTO {

    private String id;
    private String orderId;
    private Status status;

    @EqualsAndHashCode.Exclude
    private LocalDateTime time;

    @EqualsAndHashCode.Include
    public LocalDateTime time() {
        if (this.time == null) {
            return null;
        }
        return this.time.truncatedTo(ChronoUnit.SECONDS);
    }

    public Order getOrder() {
        var order = new Order();
        order.setId(this.orderId);
        return order;
    }

    public StatusTrackerRecord toStatusTrackerRecord() {
        var statusRecord = new StatusTrackerRecord();
        statusRecord.setId(getId());
        statusRecord.setOrder(getOrder());
        statusRecord.setStatus(getStatus());
        statusRecord.setTime(time());
        return statusRecord;
    }

    public static List<StatusTrackerRecord> toStatusTrackerRecords(Collection<StatusTrackerRecordDTO> statusTrackerRecordsDTO) {
        return statusTrackerRecordsDTO.stream()
                .map(StatusTrackerRecordDTO::toStatusTrackerRecord)
                .toList();
    }
}
