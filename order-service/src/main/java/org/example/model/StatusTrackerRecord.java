package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "status-tracker")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class StatusTrackerRecord {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private String orderId;

    private String status;
    private LocalDateTime time;

    @JsonIgnore
    public Status getStatusObj() {
        return Status.valueOf(status);
    }
}
