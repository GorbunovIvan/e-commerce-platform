package org.example.model.dto;

import lombok.*;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class StatusTrackerRecordDTO {

    private String orderId;
    private Status status;
    private LocalDateTime time;

    public StatusTrackerRecord toStatusTrackerRecord() {
        return new StatusTrackerRecord(null, orderId, status.name(), time);
    }
}
