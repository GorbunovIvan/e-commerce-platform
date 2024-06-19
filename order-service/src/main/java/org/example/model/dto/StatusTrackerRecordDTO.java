package org.example.model.dto;

import lombok.*;
import org.example.model.Status;
import org.example.model.StatusTrackerRecord;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class StatusTrackerRecordDTO {

    private String orderId;
    private Status status;

    @EqualsAndHashCode.Exclude
    private LocalDateTime time;

    public StatusTrackerRecord toStatusTrackerRecord() {
        return new StatusTrackerRecord(null, orderId, status.name(), time);
    }

    @EqualsAndHashCode.Include
    public LocalDateTime time() {
        if (this.time == null) {
            return null;
        }
        return this.time.truncatedTo(ChronoUnit.SECONDS);
    }
}
